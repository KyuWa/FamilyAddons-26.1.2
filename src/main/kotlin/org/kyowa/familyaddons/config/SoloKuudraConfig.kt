package org.kyowa.familyaddons.config

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.*

class SoloKuudraConfig {

    // ── Gorilla Tactics Timer ─────────────────────────────────────────
    @Expose @JvmField
    @ConfigOption(name = "Gorilla Tactics Timer", desc = "")
    @ConfigEditorAccordion(id = 50)
    var gorillaAccordion = false

    @Expose @JvmField
    @ConfigAccordionId(id = 50)
    @ConfigOption(name = "Enable", desc = "Show a 3-second countdown when the Tactical Insertion ability is used.")
    @ConfigEditorBoolean
    var gorillaTacticsTimer = false

    @Expose @JvmField
    @ConfigAccordionId(id = 50)
    @ConfigOption(name = "Display Unit", desc = "Seconds: 3.00s → 0.00s. Ticks: 60t → 0t (1 tick = 0.05s).")
    @ConfigEditorDropdown(values = ["Seconds", "Ticks"])
    var gorillaDisplayUnit = 0

    @Expose @JvmField var gorillaHudX = -1
    @Expose @JvmField var gorillaHudY = -1
    @Expose @JvmField var gorillaHudScale = "1.5"

    // ── Pearl Timer ───────────────────────────────────────────────────
    @Expose @JvmField
    @ConfigOption(name = "Pearl Timer", desc = "")
    @ConfigEditorAccordion(id = 51)
    var pearlAccordion = false

    @Expose @JvmField
    @ConfigAccordionId(id = 51)
    @ConfigOption(name = "Enable", desc = "Show a countdown for each thrown ender pearl until it lands. Supports multiple pearls in flight.")
    @ConfigEditorBoolean
    var pearlTimer = false

    @Expose @JvmField
    @ConfigAccordionId(id = 51)
    @ConfigOption(name = "Display Unit", desc = "Seconds: 1.20s → 0.00s. Ticks: 24t → 0t (1 tick = 0.05s).")
    @ConfigEditorDropdown(values = ["Seconds", "Ticks"])
    var pearlDisplayUnit = 0

    @Expose @JvmField var pearlTimerHudX = -1
    @Expose @JvmField var pearlTimerHudY = -1
    @Expose @JvmField var pearlTimerHudScale = "1.0"
}