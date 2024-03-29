package nl.sajansen.canmoduleinterface.gui.config

import nl.sajansen.canmoduleinterface.config.PropertyLoader
import nl.sajansen.canmoduleinterface.gui.config.formcomponents.*
import nl.sajansen.canmoduleinterface.gui.notifications.Notifications
import org.slf4j.LoggerFactory
import java.awt.BorderLayout
import java.awt.GridLayout
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.border.EmptyBorder

class ConfigEditPanel : JPanel() {
    private val logger = LoggerFactory.getLogger(ConfigEditPanel::class.java.name)

    private val formComponents: ArrayList<FormComponent> = ArrayList()

    init {
        createFormInputs()
        createGui()
    }

    private fun createFormInputs() {
        formComponents.add(HeaderFormComponent("Serial"))
        formComponents.add(NumberFormInput("serialComBaudRate", "Baud rate", min = 0, max = Int.MAX_VALUE, step = 100))
        formComponents.add(StringFormInput("serialStringBootDone", "Boot-done string", allowEmpty = true, toolTipText = "The string the serial device will sent when booting is finished"))

        formComponents.add(HeaderFormComponent(""))
        formComponents.add(HeaderFormComponent("Graphics"))
        formComponents.add(NumberFormInput("fps", "GUI frames per second", min = 0, max = 1000, step = 1))
        formComponents.add(NumberFormInput("activeTimeout", "Highlight time for active component (ms)", min = 0, max = Long.MAX_VALUE, step = 500))
        formComponents.add(NumberFormInput("inactiveTimeout", "Fade out time for inactive component (ms)", min = 0, max = Long.MAX_VALUE, step = 500))
        formComponents.add(NumberFormInput("inactiveComponentOpacity", "Inactive component opacity", min = 0.0f, max = 1.0f, step = 0.1f))

        formComponents.add(HeaderFormComponent(""))
        formComponents.add(HeaderFormComponent("Other"))
        formComponents.add(BooleanFormInput("mainWindowAlwaysOnTop", "Keep window always on top"))
        formComponents.add(BooleanFormInput("updatesCheckForUpdates", "Check for updates"))
    }

    private fun createGui() {
        layout = BorderLayout()

        val mainPanel = JPanel()
        mainPanel.layout = GridLayout(0, 1)
        mainPanel.border = EmptyBorder(10, 10, 10, 10)

        addConfigItems(mainPanel)

        val scrollPanelInnerPanel = JPanel(BorderLayout())
        scrollPanelInnerPanel.add(mainPanel, BorderLayout.PAGE_START)
        val scrollPanel = JScrollPane(scrollPanelInnerPanel)
        scrollPanel.border = null
        add(scrollPanel, BorderLayout.CENTER)
    }

    private fun addConfigItems(panel: JPanel) {
        formComponents.forEach {
            try {
                panel.add(it.component())
            } catch (e: Exception) {
                logger.error("Failed to create Config Edit GUI component: ${it::class.java}")
                e.printStackTrace()

                if (it !is FormInput) {
                    return@forEach
                }

                logger.error("Failed to create Config Edit GUI component: ${it.key}")
                Notifications.popup(
                    "Failed to load GUI input for config key: <strong>${it.key}</strong>. Delete your <i>${PropertyLoader.getPropertiesFile().name}</i> file and try again. (Error: ${e.localizedMessage})",
                    "Configuration"
                )
                panel.add(TextFormComponent("Failed to load component. See Notifications.").component())
            }
        }
    }

    fun saveAll(): Boolean {
        val formInputComponents = formComponents.filterIsInstance<FormInput>()
        val validationErrors = ArrayList<String>()

        formInputComponents.forEach {
            val validation = it.validate()
            if (validation.isEmpty()) {
                return@forEach
            }

            logger.warn(validation.toString())
            validationErrors += validation
        }

        if (validationErrors.isNotEmpty()) {
            if (this.parent == null) {
                logger.warn("Panel is not a visible GUI component")
                return false
            }

            JOptionPane.showMessageDialog(
                this, validationErrors.joinToString(",\n"),
                "Invalid data",
                JOptionPane.ERROR_MESSAGE
            )
            return false
        }

        formInputComponents.forEach { it.save() }
        return true
    }
}
