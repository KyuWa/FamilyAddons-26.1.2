package org.kyowa.familyaddons.config

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.*

class BestiaryConfig {

    @Expose @JvmField
    @ConfigOption(name = "Enable HUD", desc = "Show the Bestiary tracker HUD on screen.")
    @ConfigEditorBoolean
    var enabled = false

    @Expose @JvmField
    @ConfigOption(name = "Display Mode", desc = "Total: all-time kills for this mob. Session: kills + uptime this session.")
    @ConfigEditorDropdown(values = ["Total", "Session"])
    var displayMode = 0  // 0 = Total, 1 = Session

    @Expose @JvmField
    @ConfigOption(name = "Auto Detect Mob", desc = "Automatically use the first mob in the Bestiary tablist section as the tracked mob. Leave Mob Name blank to use this.")
    @ConfigEditorBoolean
    var autoMobName = false

    @Expose @JvmField
    @ConfigOption(name = "Mob Name", desc = "Manually set the mob to track (e.g. 'Ghost'). Leave blank to use Auto Detect. HUD title will be '[Name] Bestiary'.")
    @ConfigEditorText
    var mobName = ""

    // ── Zone-based ESP ────────────────────────────────────────────────
    @Expose @JvmField
    @ConfigOption(name = "Zone Highlight", desc = "Highlight all non-maxed bestiary mobs in the selected zone. Refreshes every 30 seconds.")
    @ConfigEditorBoolean
    var zoneHighlightEnabled = false

    @Expose @JvmField
    @ConfigOption(name = "Bestiary Zone", desc = "Select the zone to highlight non-maxed mobs for.")
    @ConfigEditorDropdown(values = ["None", "Island", "Hub", "The Farming Lands", "The Garden", "Spider's Den", "The End", "Crimson Isle", "Deep Caverns", "Dwarven Mines", "Crystal Hollows", "The Park", "Galatea", "Spooky Festival", "The Catacombs", "Fishing", "Mythological Creatures", "Jerry", "Kuudra"])
    var bestiaryZone = 0  // 0 = None

    // ── Persisted total kills per mob name ────────────────────────────
    @Expose @JvmField
    var savedKills: MutableMap<String, Int> = mutableMapOf()

    // ── Persisted maxed mob names (zone highlight — survives restarts) ─
    @Expose @JvmField
    var maxedMobs: MutableSet<String> = mutableSetOf()

    // ── HUD position/scale (saved by HUD editor) ──────────────────────
    @Expose var hudX: Int = 10
    @Expose var hudY: Int = 10
    @Expose var hudScale: Float = 1.0f
}