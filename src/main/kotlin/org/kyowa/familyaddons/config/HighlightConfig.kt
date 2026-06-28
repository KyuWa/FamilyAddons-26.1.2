package org.kyowa.familyaddons.config

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorColour
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorDropdown
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorSlider
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorText
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class HighlightConfig {
    @Expose @JvmField
    @ConfigOption(name = "Enable Highlight", desc = "Draw ESP boxes around entities matching the list below.")
    @ConfigEditorBoolean
    var enabled = true

    @Expose @JvmField
    @ConfigOption(name = "Mob Names", desc = "Comma-separated list of mob names to highlight. Case insensitive.")
    @ConfigEditorText
    var mobNames = ""

    @Expose @JvmField
    @ConfigOption(name = "Color", desc = "Color of the ESP box.")
    @ConfigEditorColour
    var color = "0:255:255:0:0"

    @Expose @JvmField
    @ConfigOption(name = "Drawing Style", desc = "How to draw the highlight.")
    @ConfigEditorDropdown(values = ["AABB", "Outline"])
    var drawingStyle = 0

    @Expose @JvmField
    @ConfigOption(name = "Tracer Lines", desc = "Draw lines from your crosshair to the nearest highlighted mobs.")
    @ConfigEditorBoolean
    var tracerEnabled = false

    @Expose @JvmField
    @ConfigOption(name = "Tracer Count", desc = "How many of the closest highlighted mobs to draw tracers to (1–20).")
    @ConfigEditorSlider(minValue = 1f, maxValue = 20f, minStep = 1f)
    var tracerCount = 5f

    @Expose @JvmField
    @ConfigOption(name = "Tracer Range", desc = "Maximum distance in chunks to draw tracers. Mobs further than this are ignored (ESP is limited to 4 chunks so prob that is best).")
    @ConfigEditorSlider(minValue = 2f, maxValue = 16f, minStep = 1f)
    var tracerChunkRange = 4f
}