package nl.sajansen.canmoduleinterface.gui.can

import nl.sajansen.canmoduleinterface.config.Config
import nl.sajansen.canmoduleinterface.events.EventsDispatcher
import nl.sajansen.canmoduleinterface.events.GuiEventListener
import nl.sajansen.canmoduleinterface.events.SerialEventListener
import nl.sajansen.canmoduleinterface.gui.can.components.CanComponentPanel
import nl.sajansen.canmoduleinterface.hardware.CAN
import nl.sajansen.canmoduleinterface.serial.SerialConnectionState
import nl.sajansen.canmoduleinterface.utils.gui.WrapLayout
import org.slf4j.LoggerFactory
import java.awt.*
import javax.swing.*
import javax.swing.border.EmptyBorder

class StatusPanel : JPanel(), GuiEventListener, SerialEventListener {
    private val logger = LoggerFactory.getLogger(StatusPanel::class.java.name)

    private val componentPanel = JPanel()
    private val canComponentPanels = arrayListOf<CanComponentPanel>()
    private val infoLabel = JLabel()

    private val screenUpdateTimer: Timer
    private var isRepainting = false

    init {
        EventsDispatcher.register(this)

        createGui()

        onComponentsListUpdated()
        onSerialConnectionChanged(CAN.state)

        screenUpdateTimer = Timer((1000 / Config.fps)) {
            screenUpdateTimerStep()
        }
        screenUpdateTimer.start()
    }

    private fun createGui() {
        layout = BorderLayout()

        infoLabel.font = Font("Dialog", Font.BOLD, 48)
        infoLabel.foreground = Color(220, 220, 220)
        infoLabel.horizontalAlignment = SwingConstants.CENTER
        infoLabel.alignmentX = Component.CENTER_ALIGNMENT
        infoLabel.border = EmptyBorder(30, 10, 10, 10)
        add(infoLabel, BorderLayout.PAGE_START)

        componentPanel.layout = WrapLayout(WrapLayout.LEADING, 10, 10)
        val scrollPane = JScrollPane(componentPanel)
        scrollPane.border = null
        add(scrollPane, BorderLayout.CENTER)
    }

    override fun onComponentsListUpdated() {
        if (CAN.components.size == canComponentPanels.size) {
            logger.info("Nothing to update as the size are the same")
            return
        }

        // Update panels list with new components
        CAN.components.forEach { component ->
            val panel = canComponentPanels.find { it.component.id == component.id }
            if (panel == null) {
                canComponentPanels.add(CanComponentPanel(component))
            }
        }

        canComponentPanels.sortBy { it.component.id }

        // Update GUI with new panels
        remove(infoLabel)
        componentPanel.removeAll()

        canComponentPanels.forEach {
            componentPanel.add(it)
        }
        revalidate()
    }

    override fun onSerialConnectionChanged(value: SerialConnectionState) {
        val text = when (value) {
            SerialConnectionState.NotConnected -> "Start connection<br/>(ctrl+alt+c)"
            SerialConnectionState.Connecting -> "Connecting..."
            SerialConnectionState.Booting -> "Booting..."
            SerialConnectionState.Running -> "Waiting for<br/>new messages..."
        }
        infoLabel.text = "<html><center>$text</center></html>"
    }

    private fun screenUpdateTimerStep() {
        if (isRepainting) {
            logger.debug("Skipping paint update: still updating")
            return
        }

        repaint()
    }

    override fun paint(g: Graphics) {
        isRepainting = true
        super.paint(g)
        isRepainting = false
    }
}