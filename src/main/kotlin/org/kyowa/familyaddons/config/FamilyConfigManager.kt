package org.kyowa.familyaddons.config

import com.google.gson.GsonBuilder
import io.github.notenoughupdates.moulconfig.common.IMinecraft
import io.github.notenoughupdates.moulconfig.gui.GuiElement
import io.github.notenoughupdates.moulconfig.gui.MoulConfigEditor
import io.github.notenoughupdates.moulconfig.processor.BuiltinMoulConfigGuis
import io.github.notenoughupdates.moulconfig.processor.ConfigProcessorDriver
import io.github.notenoughupdates.moulconfig.processor.MoulConfigProcessor
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object FamilyConfigManager {

    private val gson = GsonBuilder()
        .excludeFieldsWithoutExposeAnnotation()
        .setPrettyPrinting()
        .create()

    private val configFile get() = File(net.minecraft.client.Minecraft.getInstance().gameDirectory, "config/familyaddons/config.json")

    private var _config: FamilyConfig = FamilyConfig()
    val config: FamilyConfig get() = _config

    private lateinit var processor: MoulConfigProcessor<FamilyConfig>
    private lateinit var driver: ConfigProcessorDriver
    private lateinit var editor: MoulConfigEditor<FamilyConfig>
    private var editorInitialized = false

    private val scheduler = Executors.newSingleThreadScheduledExecutor()

    fun load() {
        configFile.parentFile.mkdirs()
        loadConfig()

        processor = MoulConfigProcessor(_config)
        BuiltinMoulConfigGuis.addProcessors(processor)
        driver = ConfigProcessorDriver(processor)
        driver.processConfig(_config)

        scheduler.scheduleAtFixedRate({ save() }, 60, 60, TimeUnit.SECONDS)
    }

    private fun loadConfig() {
        if (!configFile.exists()) {
            _config = FamilyConfig()
            save()
            return
        }
        try {
            FileReader(configFile).use { fr ->
                val loaded = gson.fromJson(fr, FamilyConfig::class.java)
                _config = loaded ?: FamilyConfig()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _config = FamilyConfig()
        }
    }

    fun save() {
        try {
            configFile.parentFile.mkdirs()
            FileWriter(configFile).use { fw -> fw.write(gson.toJson(_config)) }
            // Rescan highlights when config changes
            net.minecraft.client.Minecraft.getInstance().execute {
                org.kyowa.familyaddons.features.EntityHighlight.rescan()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getEditor(): MoulConfigEditor<FamilyConfig> {
        if (!editorInitialized) {
            editor = MoulConfigEditor(processor)
            editorInitialized = true
        }
        return editor
    }

    fun openGui() {
        IMinecraft.getInstance().openWrappedScreen(getEditor())
    }
}
