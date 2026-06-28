package org.kyowa.familyaddons.features

import com.google.gson.JsonParser
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.CompletableFuture
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft
import org.kyowa.familyaddons.COLOR_CODE_REGEX
import org.kyowa.familyaddons.KeyFetcher
import org.kyowa.familyaddons.FamilyAddons
import org.kyowa.familyaddons.config.FamilyConfigManager

object BestiaryZoneHighlight {

    val ZONES = listOf(
        "None", "Island", "Hub", "The Farming Lands", "The Garden", "Spider's Den",
        "The End", "Crimson Isle", "Deep Caverns", "Dwarven Mines", "Crystal Hollows",
        "The Park", "Galatea", "Spooky Festival", "The Catacombs", "Fishing",
        "Mythological Creatures", "Jerry", "Kuudra"
    )

    private val ZONE_TO_NEU_KEY = mapOf(
        "Island" to "dynamic", "Hub" to "hub", "The Farming Lands" to "farming_1",
        "The Garden" to "garden", "Spider's Den" to "combat_1", "The End" to "combat_3",
        "Crimson Isle" to "crimson_isle", "Deep Caverns" to "mining_2",
        "Dwarven Mines" to "mining_3", "Crystal Hollows" to "crystal_hollows",
        "The Park" to "foraging_1", "Galatea" to "foraging_2",
        "Spooky Festival" to "spooky_festival", "The Catacombs" to "catacombs",
        "Fishing" to "fishing", "Mythological Creatures" to "mythological_creatures",
        "Jerry" to "jerry", "Kuudra" to "kuudra"
    )

    @Volatile var allZoneMobNames: Set<String> = emptySet()
        private set

    @Volatile var activeMobNames: Set<String> = emptySet()
        private set

    private val httpClient = HttpClient.newHttpClient()
    private var tickCounter = 0

    private data class MobEntry(val displayName: String, val mobIds: List<String>, val maxKills: Long)
    private var repoData: Map<String, List<MobEntry>> = emptyMap()
    private var repoLoaded = false

    private val NAME_REMAPS = mapOf("sneaky creeper" to "Creeper")

    // Track previous config to detect changes
    private var lastZoneIndex: Int = -1
    private var lastZoneHighlightEnabled: Boolean = false

    fun register() {
        ClientTickEvents.END_CLIENT_TICK.register { _ ->
            val cfg = FamilyConfigManager.config.bestiary

            val zoneChanged = cfg.bestiaryZone != lastZoneIndex
            val enabledChanged = cfg.zoneHighlightEnabled != lastZoneHighlightEnabled
            lastZoneIndex = cfg.bestiaryZone
            lastZoneHighlightEnabled = cfg.zoneHighlightEnabled

            if (!cfg.zoneHighlightEnabled) {
                if (activeMobNames.isNotEmpty()) { activeMobNames = emptySet(); allZoneMobNames = emptySet() }
                return@register
            }
            if (cfg.bestiaryZone == 0) {
                if (activeMobNames.isNotEmpty()) { activeMobNames = emptySet(); allZoneMobNames = emptySet() }
                return@register
            }

            // Refresh immediately when zone or toggle changes
            if (zoneChanged || enabledChanged) {
                tickCounter = 0
                FamilyAddons.LOGGER.info("BestiaryZoneHighlight: config changed — refreshing immediately")
                refresh()
                return@register
            }

            // Otherwise periodic refresh every 30s
            if (tickCounter++ % 600 != 0) return@register
            refresh()
        }
    }

    fun refresh() {
        CompletableFuture.runAsync {
            try {
                if (!repoLoaded) loadRepo()

                val cfg = FamilyConfigManager.config.bestiary
                val zoneIndex = cfg.bestiaryZone
                if (zoneIndex <= 0 || zoneIndex >= ZONES.size) { activeMobNames = emptySet(); return@runAsync }
                val zoneName = ZONES[zoneIndex]
                val neuKey = ZONE_TO_NEU_KEY[zoneName] ?: run {
                    FamilyAddons.LOGGER.warn("BestiaryZoneHighlight: no key mapped for '$zoneName'")
                    activeMobNames = emptySet()
                    return@runAsync
                }

                val zoneMobs = repoData[neuKey]
                if (zoneMobs.isNullOrEmpty()) {
                    FamilyAddons.LOGGER.warn("BestiaryZoneHighlight: zone '$neuKey' has no mobs in repo")
                    activeMobNames = emptySet()
                    return@runAsync
                }

                val fullSet = mutableSetOf<String>()
                for (mob in zoneMobs) {
                    val cleanName = mob.displayName.replace(Regex("§[0-9a-fk-or]"), "").trim()
                    fullSet.add(NAME_REMAPS[cleanName.lowercase()] ?: cleanName)
                }
                allZoneMobNames = fullSet
                FamilyAddons.LOGGER.info("BestiaryZoneHighlight: $zoneName zone loaded — ${fullSet.size} mobs: $fullSet")

                // Apply persisted maxed mobs immediately
                val persistedMaxed = cfg.maxedMobs
                if (persistedMaxed.isNotEmpty()) {
                    val filtered = fullSet.minus(persistedMaxed)
                    if (filtered != activeMobNames) {
                        activeMobNames = filtered
                        Minecraft.getInstance().execute { EntityHighlight.rescan() }
                        FamilyAddons.LOGGER.info("BestiaryZoneHighlight: persisted MAX applied — ${filtered.size} active")
                    }
                }

                checkMaxFromTablist()

                val apiKey = KeyFetcher.getApiKey()
                if (!apiKey.isNullOrBlank()) {
                    val player = Minecraft.getInstance().player
                    if (player != null) {
                        val uuid = player.gameProfile.id.toString().replace("-", "")
                        val data = get("https://api.hypixel.net/v2/skyblock/profiles?uuid=$uuid&key=$apiKey")
                        if (data?.get("success")?.asBoolean == true) {
                            val profiles = data.getAsJsonArray("profiles")
                            val profile = profiles?.map { it.asJsonObject }
                                ?.firstOrNull { it.get("selected")?.asBoolean == true }
                                ?: profiles?.lastOrNull()?.asJsonObject
                            val member = profile?.getAsJsonObject("members")?.getAsJsonObject(uuid)
                            val killsObj = member?.getAsJsonObject("bestiary")?.getAsJsonObject("kills")
                            if (killsObj != null) {
                                FamilyAddons.LOGGER.info("BestiaryZoneHighlight: API killsObj keys sample: ${killsObj.keySet().take(5)}")
                                val apiMaxed = mutableSetOf<String>()
                                for (mob in zoneMobs) {
                                    val cleanName = mob.displayName.replace(Regex("§[0-9a-fk-or]"), "").trim()
                                    val mappedName = NAME_REMAPS[cleanName.lowercase()] ?: cleanName
                                    val total = mob.mobIds.sumOf { id -> killsObj.get(id)?.asLong ?: 0L }
                                    FamilyAddons.LOGGER.info("BestiaryZoneHighlight: mob '$cleanName' ids=${mob.mobIds} total=$total max=${mob.maxKills}")
                                    if (total >= mob.maxKills) apiMaxed.add(mappedName)
                                }
                                val newApiMaxed = apiMaxed - cfg.maxedMobs
                                if (newApiMaxed.isNotEmpty()) {
                                    cfg.maxedMobs.addAll(newApiMaxed)
                                    FamilyConfigManager.save()
                                    FamilyAddons.LOGGER.info("BestiaryZoneHighlight: persisted API maxed mobs: $newApiMaxed")
                                }
                                val allMaxed = apiMaxed + cfg.maxedMobs
                                val combined = allZoneMobNames.minus(allMaxed)
                                if (combined != activeMobNames) {
                                    activeMobNames = combined
                                    Minecraft.getInstance().execute { EntityHighlight.rescan() }
                                    FamilyAddons.LOGGER.info("BestiaryZoneHighlight: API MAX check → ${combined.size} active: $combined")
                                    if (apiMaxed.isNotEmpty()) FamilyAddons.LOGGER.info("BestiaryZoneHighlight: API maxed: $apiMaxed")
                                }
                            }
                        }
                    }
                }

            } catch (e: Exception) {
                FamilyAddons.LOGGER.warn("BestiaryZoneHighlight error: ${e.message}")
            }
        }
    }

    fun checkMaxFromTablist() {
        if (!FamilyConfigManager.config.bestiary.zoneHighlightEnabled) return
        if (allZoneMobNames.isEmpty()) return

        val cfg = FamilyConfigManager.config.bestiary
        val maxed = readMaxedMobsFromTablist()

        val newMaxed = maxed - cfg.maxedMobs
        if (newMaxed.isNotEmpty()) {
            cfg.maxedMobs.addAll(newMaxed)
            FamilyConfigManager.save()
            FamilyAddons.LOGGER.info("BestiaryZoneHighlight: persisted new maxed mobs: $newMaxed")
        }

        val allMaxed = maxed + cfg.maxedMobs
        val filtered = allZoneMobNames.minus(allMaxed)
        if (filtered != activeMobNames) {
            activeMobNames = filtered
            Minecraft.getInstance().execute { EntityHighlight.rescan() }
            FamilyAddons.LOGGER.info("BestiaryZoneHighlight: MAX check → ${filtered.size} active: $filtered")
        }
    }

    private fun readMaxedMobsFromTablist(): Set<String> {
        val tabList = Minecraft.getInstance().connection?.onlinePlayers ?: return emptySet()
        val maxed = mutableSetOf<String>()
        val pattern = Regex("""^\s+(.+?)\s+(\d+):\s+MAX\s*$""", RegexOption.IGNORE_CASE)
        for (entry in tabList) {
            val raw = entry.tabListDisplayName?.string ?: continue
            val clean = raw.replace(COLOR_CODE_REGEX, "")
            val match = pattern.matchEntire(clean) ?: continue
            val mobNameRaw = match.groupValues[1].trim()
            if (mobNameRaw.isNotBlank()) maxed.add(NAME_REMAPS[mobNameRaw.lowercase()] ?: mobNameRaw)
        }
        return maxed
    }

    private fun loadRepo() {
        try {
            val json = getRaw("https://raw.githubusercontent.com/NotEnoughUpdates/NotEnoughUpdates-REPO/master/constants/bestiary.json") ?: return
            val root = JsonParser.parseString(json).asJsonObject
            val result = mutableMapOf<String, List<MobEntry>>()
            val brackets = mutableMapOf<Int, List<Long>>()
            root.getAsJsonObject("brackets")?.let { bracketsObj ->
                for ((k, v) in bracketsObj.entrySet()) {
                    val num = k.toIntOrNull() ?: continue
                    brackets[num] = v.asJsonArray.map { it.asLong }
                }
            }
            for ((zoneKey, zoneVal) in root.entrySet()) {
                if (zoneKey == "brackets") continue
                val entries = mutableListOf<MobEntry>()
                fun parseMobsArray(obj: com.google.gson.JsonObject) {
                    obj.getAsJsonArray("mobs")?.forEach { mobEl ->
                        val mobObj = mobEl.asJsonObject
                        val name = mobObj.get("name")?.asString ?: return@forEach
                        val mobIds = mobObj.getAsJsonArray("mobs")?.map { it.asString }
                            ?: listOf(name.lowercase().replace(" ", "_"))
                        val bracket = mobObj.get("bracket")?.asInt ?: 1
                        val tierList = brackets[bracket] ?: listOf(250L)
                        entries.add(MobEntry(name, mobIds, tierList.last()))
                    }
                }
                try {
                    val zoneObj = zoneVal.asJsonObject
                    if (zoneObj.has("mobs")) {
                        parseMobsArray(zoneObj)
                    } else {
                        for ((_, subVal) in zoneObj.entrySet()) {
                            try {
                                val subObj = subVal.asJsonObject
                                if (subObj.has("mobs")) parseMobsArray(subObj)
                                else for ((_, subSubVal) in subObj.entrySet()) {
                                    try { parseMobsArray(subSubVal.asJsonObject) } catch (_: Exception) {}
                                }
                            } catch (_: Exception) {}
                        }
                    }
                } catch (_: Exception) {}
                if (entries.isNotEmpty()) result[zoneKey] = entries
            }
            repoData = result
            repoLoaded = true
            FamilyAddons.LOGGER.info("BestiaryZoneHighlight: repo loaded — ${result.size} zones")
            for ((zone, mobs) in result) {
                FamilyAddons.LOGGER.info("BestiaryZoneHighlight: zone '$zone' mobs: ${mobs.map { it.displayName }}")
            }
        } catch (e: Exception) {
            FamilyAddons.LOGGER.warn("BestiaryZoneHighlight: repo load failed: ${e.message}")
        }
    }

    private fun get(url: String) = try {
        val req = HttpRequest.newBuilder().uri(URI.create(url)).header("User-Agent", "FamilyAddons/1.0").GET().build()
        JsonParser.parseString(httpClient.send(req, HttpResponse.BodyHandlers.ofString()).body()).asJsonObject
    } catch (e: Exception) { null }

    private fun getRaw(url: String) = try {
        val req = HttpRequest.newBuilder().uri(URI.create(url)).header("User-Agent", "FamilyAddons/1.0").GET().build()
        httpClient.send(req, HttpResponse.BodyHandlers.ofString()).body()
    } catch (e: Exception) { null }
}