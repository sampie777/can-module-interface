package nl.sajansen.canmoduleinterface.config

import nl.sajansen.canmoduleinterface.ApplicationInfo
import nl.sajansen.canmoduleinterface.utils.getCurrentJarDirectory
import org.slf4j.LoggerFactory
import java.awt.Color
import java.awt.Dimension
import java.awt.Point
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.reflect.Modifier
import java.util.*

object PropertyLoader {
    private val logger = LoggerFactory.getLogger(PropertyLoader.toString())

    // En-/disables the creation of a properties file and writing to a properties file.
    // Leave disabled when running tests.
    var writeToFile: Boolean = false

    private val userPropertiesFile = File(
        getCurrentJarDirectory(this).absolutePath + File.separatorChar + "${ApplicationInfo.name}.properties"
    )
    private var userProperties = Properties()

    private const val defaultValueDelimiter = ","

    fun load() {
        loadUserProperties()
    }

    fun getPropertiesFile(): File {
        return userPropertiesFile
    }

    fun getUserProperties(): Properties {
        return userProperties
    }

    private fun loadUserProperties() {
        logger.info("Loading user properties from file: " + userPropertiesFile.absolutePath)

        if (createNewPropertiesFile()) {
            return
        }

        val userProperties = Properties()

        FileInputStream(userPropertiesFile).use { fileInputStream -> userProperties.load(fileInputStream) }

        PropertyLoader.userProperties = userProperties
    }

    fun save() {
        saveUserPropertiesToFIle()
    }

    private fun saveUserPropertiesToFIle() {
        logger.info("Saving user properties")

        if (!writeToFile) {
            logger.info("writeToFile is turned off, so not saving properties to file")
            return
        }

        createNewPropertiesFile()

        FileOutputStream(userPropertiesFile).use { fileOutputStream ->
            userProperties.store(
                fileOutputStream,
                "User properties for ${ApplicationInfo.name}"
            )
        }
    }

    fun loadConfig(configClass: Class<*>) {
        try {
            for (field in configClass.declaredFields) {
                if (Modifier.isFinal(field.modifiers)) {
                    continue
                }

                logger.debug("Loading config field: ${field.name}")

                try {
                    if (!Modifier.isStatic(field.modifiers)) {
                        continue
                    }

                    field.isAccessible = true

                    val propertyValue = userProperties.getProperty(field.name)
                        ?: throw IllegalArgumentException("Missing configuration value: ${field.name}")

                    field.set(null, stringToTypedValue(propertyValue, field.name, field.type))

                } catch (e: IllegalArgumentException) {
                    logger.debug(e.toString())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("Error loading configuration: $e", e)
        }
    }

    /**
     * Populates the userProperties object with the values from the given Config object.
     * Returns true if the values have changed, otherwise returns false
     */
    fun saveConfig(configClass: Class<*>): Boolean {
        val newProperties = Properties()

        try {
            for (field in configClass.declaredFields) {
                if (field.name == "INSTANCE" || field.name == "logger") {
                    continue
                }

                try {
                    if (!Modifier.isStatic(field.modifiers)) {
                        continue
                    }

                    field.isAccessible = true
                    val configValue = field.get(Config)

                    logger.trace("Saving config field: ${field.name} with value: $configValue")
                    typedValueToPropertyValue(newProperties, field.name, field.type, configValue)

                } catch (e: IllegalArgumentException) {
                    logger.warn(e.toString())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("Error saving configuration: $e", e)
        }

        if (userProperties == newProperties) {
            return false
        }
        userProperties = newProperties
        return true
    }

    fun stringToTypedValue(value: String, name: String, type: Class<*>): Any? {
        return when (type) {
            String::class.java -> value
            Boolean::class.javaPrimitiveType -> java.lang.Boolean.parseBoolean(value)
            Int::class.javaPrimitiveType -> value.toInt()
            Float::class.javaPrimitiveType -> value.toFloat()
            Long::class.javaPrimitiveType -> value.toLong()
            Double::class.javaPrimitiveType -> value.toDouble()
            Color::class.java -> {
                val rgb = value.split(defaultValueDelimiter)
                if (rgb.size < 3) {
                    throw IllegalArgumentException("Configuration parameter '$name' has invalid value: $value")
                }
                Color(rgb[0].toInt(), rgb[1].toInt(), rgb[2].toInt())
            }
            Point::class.java -> {
                val values = value.split(defaultValueDelimiter)
                if (values.size != 2) {
                    throw IllegalArgumentException("Configuration parameter '$name' has invalid value: $value")
                }
                Point(values[0].toInt(), values[1].toInt())
            }
            Dimension::class.java -> {
                val values = value.split(defaultValueDelimiter)
                if (values.size != 2) {
                    throw IllegalArgumentException("Configuration parameter '$name' has invalid value: $value")
                }
                Dimension(values[0].toInt(), values[1].toInt())
            }
            else -> throw IllegalArgumentException("Unknown configuration value type: " + type.name)
        }

    }

    private fun typedValueToPropertyValue(props: Properties, name: String, type: Class<*>, value: Any?) {
        if (value == null) {
            props.setProperty(name, "")
            return
        }

        val stringValue = when (type) {
            Color::class.java -> {
                val color = value as Color
                listOf(color.red, color.green, color.blue).joinToString(defaultValueDelimiter)
            }
            Point::class.java -> {
                val point = value as Point
                point.x.toString() + defaultValueDelimiter + point.y
            }
            Dimension::class.java -> {
                val dimension = value as Dimension
                dimension.width.toString() + defaultValueDelimiter + dimension.height
            }
            else -> value.toString()
        }

        props.setProperty(name, stringValue)
    }

    private fun createNewPropertiesFile(): Boolean {
        if (userPropertiesFile.exists()) {
            return false
        }

        if (!writeToFile) {
            logger.info("writeToFile is turned off, so not creating a new file")
            return true
        }

        logger.info("Creating file: " + userPropertiesFile.absolutePath)
        userPropertiesFile.createNewFile()
        return true
    }
}