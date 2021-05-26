package nl.sajansen.canmoduleinterface.gui.mainFrame


import nl.sajansen.canmoduleinterface.config.Config
import nl.sajansen.canmoduleinterface.events.EventsDispatcher
import nl.sajansen.canmoduleinterface.events.GuiEventListener
import nl.sajansen.canmoduleinterface.events.SerialEventListener
import nl.sajansen.canmoduleinterface.gui.Theme
import nl.sajansen.canmoduleinterface.hardware.CAN
import nl.sajansen.canmoduleinterface.serial.SerialConnectionState
import org.slf4j.LoggerFactory
import java.awt.BorderLayout
import java.awt.Color
import javax.swing.BorderFactory
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.border.EmptyBorder

class StatusBar : JPanel(), GuiEventListener, SerialEventListener {
    private val logger = LoggerFactory.getLogger(StatusBar::class.java.name)

    private val statusLabel = JLabel()

    init {
        EventsDispatcher.register(this)
        createGui()
        updateText()
    }

    private fun createGui() {
        layout = BorderLayout()
        border = BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Color(190, 190, 190)),
            EmptyBorder(5, 10, 5, 5)
        )

        statusLabel.foreground = Color(130, 130, 130)
        statusLabel.font = Theme.smallFont
        add(statusLabel, BorderLayout.LINE_START)
    }

    override fun onSerialConnectionChanged(value: SerialConnectionState) {
        updateText()
    }

    override fun onConfigSettingsSaved() {
        updateText()
    }

    private fun updateText() {
        statusLabel.text = "[${Config.serialComPort}] (${Config.serialComBaudRate}): ${CAN.state}"
    }
}