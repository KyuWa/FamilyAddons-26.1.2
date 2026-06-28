package org.kyowa.familyaddons.config

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.*

/**
 * Hidden config category. Only visible in the GUI when the player's UUID is
 * on the Cloudflare whitelist (or matches the hardcoded override list in
 * Whitelist.kt). The @Category annotation on FamilyConfig.hidden is stripped
 * at load time via reflection if the player isn't whitelisted, so non-allowed
 * users see no Hidden category at all.
 *
 * Contains private Kuudra features that aren't on the public GitHub repo.
 */
class HiddenConfig {

    // ── Crate Hitbox accordion (id=10) ────────────────────────
    // Renamed from "Crate Waypoints" since the visual is purely a hitbox + drag radius circle.
    @Expose @JvmField
    @ConfigOption(name = "Crate Hitbox", desc = "")
    @ConfigEditorAccordion(id = 10)
    var crateHitboxAccordion = false

    @Expose @JvmField
    @ConfigAccordionId(id = 10)
    @ConfigOption(name = "Enable Crate Hitbox", desc = "Highlight Kuudra supply crates and the drag radius.")
    @ConfigEditorBoolean
    var crateWaypointsEnabled = false

    @Expose @JvmField
    @ConfigAccordionId(id = 10)
    @ConfigOption(name = "Show Crate Hitbox", desc = "Draw a wireframe outline around the crate's interaction zombie.")
    @ConfigEditorBoolean
    var showCrateHitbox = true

    @Expose @JvmField
    @ConfigAccordionId(id = 10)
    @ConfigOption(name = "Crate Hitbox Color", desc = "Wireframe color for the crate hitbox.")
    @ConfigEditorColour
    var crateHitboxColor = "0:255:255:255:0"

    @Expose @JvmField
    @ConfigAccordionId(id = 10)
    @ConfigOption(name = "Crate Reach Color Change", desc = "Switch crate hitbox color when you're within reach distance.")
    @ConfigEditorBoolean
    var crateHitboxReachColorChange = true

    @Expose @JvmField
    @ConfigAccordionId(id = 10)
    @ConfigOption(name = "Crate In-Reach Color", desc = "Color when crate is in reach.")
    @ConfigEditorColour
    var crateHitboxInReachColor = "0:255:0:255:0"

    @Expose @JvmField
    @ConfigAccordionId(id = 10)
    @ConfigOption(name = "Show Drag Hitbox", desc = "Draw a circle on the ground showing the drag radius.")
    @ConfigEditorBoolean
    var showDragHitbox = true

    @Expose @JvmField
    @ConfigAccordionId(id = 10)
    @ConfigOption(name = "Drag Hitbox Color", desc = "Color for the drag radius circle.")
    @ConfigEditorColour
    var dragHitboxColor = "0:255:255:150:0"

    @Expose @JvmField
    @ConfigAccordionId(id = 10)
    @ConfigOption(name = "Drag In-Range Color Change", desc = "Switch drag color when your fishing bobber is in range.")
    @ConfigEditorBoolean
    var dragHitboxInRangeColorChange = true

    @Expose @JvmField
    @ConfigAccordionId(id = 10)
    @ConfigOption(name = "Drag In-Range Color", desc = "Color when fishing bobber is in drag range.")
    @ConfigEditorColour
    var dragHitboxInRangeColor = "0:255:0:255:0"

    // ── Pearl Waypoints accordion (id=11) ─────────────────────
    @Expose @JvmField
    @ConfigOption(name = "Pearl Waypoints", desc = "")
    @ConfigEditorAccordion(id = 11)
    var pearlWaypointsAccordion = false

    @Expose @JvmField
    @ConfigAccordionId(id = 11)
    @ConfigOption(name = "Enable Pearl Waypoints", desc = "Show dynamic pearl-throw aim points for supplies during Kuudra.")
    @ConfigEditorBoolean
    var pearlWaypointsEnabled = false

    @Expose @JvmField
    @ConfigAccordionId(id = 11)
    @ConfigOption(name = "Talisman Tier", desc = "Pearl Talisman tier — affects how long you can carry a chest before it slips.")
    @ConfigEditorDropdown(values = ["No Tali", "T1", "T2", "T3"])
    var pearlTalismanTier = 3

    @Expose @JvmField
    @ConfigAccordionId(id = 11)
    @ConfigOption(name = "Waypoint Shape", desc = "Shape of the main aim-point waypoint.")
    @ConfigEditorDropdown(values = ["AABB", "AABB Outline", "Square", "Circle"])
    var pearlShape = 1

    @Expose @JvmField
    @ConfigAccordionId(id = 11)
    @ConfigOption(name = "Waypoint Color", desc = "Color of the main aim-point waypoint.")
    @ConfigEditorColour
    var pearlColor = "0:255:80:200:255"

    @Expose @JvmField
    @ConfigAccordionId(id = 11)
    @ConfigOption(name = "Waypoint Size", desc = "Size of the main aim-point waypoint.")
    @ConfigEditorSlider(minValue = 0.1f, maxValue = 3.0f, minStep = 0.05f)
    var pearlSize = 0.1f

    // ── Timer ────────────────────────────────
    @Expose @JvmField
    @ConfigAccordionId(id = 11)
    @ConfigOption(name = "Show Timer", desc = "Show pearl flight time near the waypoint.")
    @ConfigEditorBoolean
    var pearlTimer = true

    @Expose @JvmField
    @ConfigAccordionId(id = 11)
    @ConfigOption(name = "Timer Delay", desc = "Extra delay (ms) added to the displayed timer to compensate for input lag.")
    @ConfigEditorSlider(minValue = 0f, maxValue = 500f, minStep = 10f)
    var pearlTimerDelay = 0f

    @Expose @JvmField
    @ConfigAccordionId(id = 11)
    @ConfigOption(name = "Timer Scale", desc = "Component scale of the flight-time label.")
    @ConfigEditorSlider(minValue = 0.5f, maxValue = 4.0f, minStep = 0.1f)
    var pearlTimerScale = 3.0f

    @Expose @JvmField
    @ConfigAccordionId(id = 11)
    @ConfigOption(name = "Timer Position", desc = "Position of the timer relative to the waypoint.")
    @ConfigEditorDropdown(values = ["Above", "Below", "Center"])
    var pearlTimerPos = 0

    // ── NOW sound ─────────────────────────────
    @Expose @JvmField
    @ConfigAccordionId(id = 11)
    @ConfigOption(name = "Now Sound", desc = "Play a ping the moment the throw window opens during a chest grab.")
    @ConfigEditorBoolean
    var pearlNowSound = true

    @Expose @JvmField
    @ConfigAccordionId(id = 11)
    @ConfigOption(name = "Now Sound Volume", desc = "Volume of the throw-window ping. 0 = silent.")
    @ConfigEditorSlider(minValue = 0f, maxValue = 2.0f, minStep = 0.1f)
    var pearlNowSoundVolume = 1.0f

    // ── Double Pearls ────────────────────────────────
    @Expose @JvmField
    @ConfigAccordionId(id = 11)
    @ConfigOption(name = "Double Pearls", desc = "Render a secondary aim point for chained pearls (when one is mid-air).")
    @ConfigEditorBoolean
    var pearlDPearls = false

    @Expose @JvmField
    @ConfigAccordionId(id = 11)
    @ConfigOption(name = "Hide on Missing", desc = "Hide a double-pearl waypoint when its supply has been called as missing.")
    @ConfigEditorBoolean
    var pearlHideOnMissing = true

    @Expose @JvmField
    @ConfigAccordionId(id = 11)
    @ConfigOption(name = "Double Pearl Size", desc = "Size of the double-pearl aim point.")
    @ConfigEditorSlider(minValue = 0.1f, maxValue = 3.0f, minStep = 0.05f)
    var pearlDPearlSize = 0.1f

    @Expose @JvmField
    @ConfigAccordionId(id = 11)
    @ConfigOption(name = "Double Pearl Timer", desc = "Show flight time for double-pearl waypoints.")
    @ConfigEditorBoolean
    var pearlDPearlTimer = true

    @Expose @JvmField
    @ConfigAccordionId(id = 11)
    @ConfigOption(name = "Double Pearl Land Delay", desc = "Subtract this many ms from the displayed time so it shows time-until-pearl-can-be-thrown.")
    @ConfigEditorSlider(minValue = 0f, maxValue = 1000f, minStep = 10f)
    var pearlDPearlLandDelay = 0f

    @Expose @JvmField
    @ConfigAccordionId(id = 11)
    @ConfigOption(name = "Double Pearl Timer Size", desc = "Component scale for the double-pearl timer.")
    @ConfigEditorSlider(minValue = 0.5f, maxValue = 4.0f, minStep = 0.1f)
    var pearlDPearlTimerSize = 0.8f

    @Expose @JvmField
    @ConfigAccordionId(id = 11)
    @ConfigOption(name = "Double Pearl Color", desc = "Color of the double-pearl aim point.")
    @ConfigEditorColour
    var pearlDPearlColor = "0:255:255:200:80"

    // ── Sky markers ────────────────────────────────
    @Expose @JvmField
    @ConfigAccordionId(id = 11)
    @ConfigOption(name = "Sky Markers", desc = "Render high-arc sky waypoints for distant supplies.")
    @ConfigEditorBoolean
    var pearlSkyPearls = false

    @Expose @JvmField
    @ConfigAccordionId(id = 11)
    @ConfigOption(name = "Sky Marker Size", desc = "Size of the sky marker.")
    @ConfigEditorSlider(minValue = 0.1f, maxValue = 5.0f, minStep = 0.1f)
    var pearlSkySize = 1.0f

    @Expose @JvmField
    @ConfigAccordionId(id = 11)
    @ConfigOption(name = "Sky Marker Color", desc = "Color of sky markers.")
    @ConfigEditorColour
    var pearlSkyColor = "0:200:255:80:255"

    // ── Per-spot Y offsets ────────────────────────────────
    @Expose @JvmField
    @ConfigAccordionId(id = 11)
    @ConfigOption(name = "Per-Spot Y Offsets", desc = "Enable per-spot vertical offsets to fine-tune aim height.")
    @ConfigEditorBoolean
    var pearlOffsetsEnabled = false

    @Expose @JvmField
    @ConfigAccordionId(id = 11)
    @ConfigOption(name = "Shop Offset", desc = "Y offset for Shop waypoint.")
    @ConfigEditorSlider(minValue = -2f, maxValue = 2f, minStep = 0.05f)
    var pearlShopOff = 0f

    @Expose @JvmField
    @ConfigAccordionId(id = 11)
    @ConfigOption(name = "X Offset", desc = "Y offset for X waypoint.")
    @ConfigEditorSlider(minValue = -2f, maxValue = 2f, minStep = 0.05f)
    var pearlXOff = 0f

    @Expose @JvmField
    @ConfigAccordionId(id = 11)
    @ConfigOption(name = "X-Cannon Offset", desc = "Y offset for X-Cannon waypoint.")
    @ConfigEditorSlider(minValue = -2f, maxValue = 2f, minStep = 0.05f)
    var pearlXCannonOff = 0f

    @Expose @JvmField
    @ConfigAccordionId(id = 11)
    @ConfigOption(name = "Equals Offset", desc = "Y offset for Equals waypoint.")
    @ConfigEditorSlider(minValue = -2f, maxValue = 2f, minStep = 0.05f)
    var pearlEqualsOff = 0f

    @Expose @JvmField
    @ConfigAccordionId(id = 11)
    @ConfigOption(name = "Slash Offset", desc = "Y offset for Slash waypoint.")
    @ConfigEditorSlider(minValue = -2f, maxValue = 2f, minStep = 0.05f)
    var pearlSlashOff = 0f

    @Expose @JvmField
    @ConfigAccordionId(id = 11)
    @ConfigOption(name = "Triangle Offset", desc = "Y offset for Triangle waypoint.")
    @ConfigEditorSlider(minValue = -2f, maxValue = 2f, minStep = 0.05f)
    var pearlTriangleOff = 0f

    @Expose @JvmField
    @ConfigAccordionId(id = 11)
    @ConfigOption(name = "Square Offset", desc = "Y offset for Square waypoint.")
    @ConfigEditorSlider(minValue = -2f, maxValue = 2f, minStep = 0.05f)
    var pearlSquareOff = 0f

    @Expose @JvmField
    @ConfigAccordionId(id = 11)
    @ConfigOption(name = "Use New Priority", desc = "Use the alternate routing where Shop and Triangle swap targets.")
    @ConfigEditorBoolean
    var pearlNewPrio = false
}
