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
    @ConfigOption(name = "Bestiary Zone", desc = "Select the zone to highlight bestiary mobs for. Fishing includes all fishing sub-zones (Lava, Backwater Bayou, festivals, Winter).")
    @ConfigEditorDropdown(values = ["None", "Island", "Hub", "The Farming Lands", "The Garden", "Spider's Den", "The End", "Crimson Isle", "Deep Caverns", "Dwarven Mines", "Crystal Hollows", "The Park", "Moonglade Marsh", "Spooky Festival", "The Catacombs", "Fishing", "Mythological Creatures", "Jerry", "Kuudra", "Torrhus Canyon", "Lotus Atoll", "Critter Safari"])
    var bestiaryZone = 0  // 0 = None

    @Expose @JvmField
    @ConfigOption(name = "Hide Maxed Mobs", desc = "On: maxed bestiary mobs are not highlighted. Off: highlight every mob in the zone, maxed or not.")
    @ConfigEditorBoolean
    var hideMaxedMobs = true

    @Expose @JvmField
    @ConfigOption(name = "Highlight Color", desc = "Color of the bestiary highlight (independent of the Highlight category's color).")
    @ConfigEditorColour
    var bestiaryColor = "0:255:255:170:0"

    @Expose @JvmField
    @ConfigOption(name = "Drawing Style", desc = "How to draw the bestiary highlight.")
    @ConfigEditorDropdown(values = ["AABB", "Outline"])
    var bestiaryDrawingStyle = 0

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