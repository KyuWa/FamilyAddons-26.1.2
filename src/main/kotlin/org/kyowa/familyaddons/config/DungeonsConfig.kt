package org.kyowa.familyaddons.config

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorSlider
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class DungeonsConfig {
    @Expose @JvmField
    @ConfigOption(name = "Auto Requeue", desc = "Auto requeue after each dungeon run.")
    @ConfigEditorBoolean
    var autoRequeue = false

    @Expose @JvmField
    @ConfigOption(name = "DT Title", desc = "Show a fading centered title when someone requests DT in dungeon party chat.")
    @ConfigEditorBoolean
    var dtTitle = false

    @Expose @JvmField
    @ConfigOption(name = "Check Party Size", desc = "Cancel requeue if party has less than 5 players.")
    @ConfigEditorBoolean
    var checkPartySize = false

    @Expose @JvmField
    @ConfigOption(name = "Requeue Delay", desc = "Seconds to wait before requeuing. Default 0.")
    @ConfigEditorSlider(minValue = 0f, maxValue = 10f, minStep = 1f)
    var requeueDelaySecs = 0f

    // -1 = auto-center
    @Expose @JvmField var dungeonDtTitleHudX = -1
    @Expose @JvmField var dungeonDtTitleHudY = -1
    @Expose @JvmField var dungeonDtTitleScale = "2.0"
}