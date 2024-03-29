package nl.sajansen.canmoduleinterface.config

import org.slf4j.LoggerFactory
import java.lang.reflect.Field
import java.lang.reflect.Modifier

object Config {
    private val logger = LoggerFactory.getLogger(Config.toString())

    /* ********************************** */

    // Main window
    var mainWindowAlwaysOnTop: Boolean = false
    var fps: Int = 30
    var activeTimeout: Long = 1000
    var inactiveTimeout: Long = 4000
    var inactiveComponentOpacity: Float = 0.4f

    // Other
    var updatesCheckForUpdates: Boolean = false

    // Serial
    var serialComPort: String = ""
    var serialComBaudRate: Int = 115200
    var serialStringBootDone: String = "Ready."

    fun load() {
        try {
            PropertyLoader.load()
            PropertyLoader.loadConfig(this::class.java)
        } catch (e: Exception) {
            logger.error("Failed to load Config")
            e.printStackTrace()
        }
    }

    fun save() {
        try {
            if (PropertyLoader.saveConfig(this::class.java)) {
                PropertyLoader.save()
            }
        } catch (e: Exception) {
            logger.error("Failed to save Config")
            e.printStackTrace()
        }
    }

    fun get(key: String): Any? {
        try {
            return javaClass.getDeclaredField(key).get(this)
        } catch (e: Exception) {
            logger.error("Could not get config key $key")
            e.printStackTrace()
        }
        return null
    }

    fun set(key: String, value: Any?) {
        try {
            javaClass.getDeclaredField(key).set(this, value)
        } catch (e: Exception) {
            logger.error("Could not set config key $key")
            e.printStackTrace()
        }
    }

    fun enableWriteToFile(value: Boolean) {
        PropertyLoader.writeToFile = value
    }

    fun fields(): List<Field> {
        val fields = javaClass.declaredFields.filter {
            it.name != "INSTANCE" && it.name != "logger"
                    && Modifier.isStatic(it.modifiers)
        }
        fields.forEach { it.isAccessible = true }
        return fields
    }
}