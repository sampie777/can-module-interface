package nl.sajansen.canmoduleinterface.gui.can

import nl.sajansen.canmoduleinterface.config.Config
import nl.sajansen.canmoduleinterface.events.EventsDispatcher
import nl.sajansen.canmoduleinterface.events.GuiEventListener
import nl.sajansen.canmoduleinterface.gui.can.components.CanComponentPanel
import nl.sajansen.canmoduleinterface.hardware.CAN
import nl.sajansen.canmoduleinterface.utils.gui.WrapLayout
import org.slf4j.LoggerFactory
import java.awt.BorderLayout
import java.awt.Graphics
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.Timer

class StatusPanel : JPanel(), GuiEventListener {
    private val logger = LoggerFactory.getLogger(StatusPanel::class.java.name)

    private val componentPanel = JPanel()
    private val canComponentPanels = arrayListOf<CanComponentPanel>()
    private val screenUpdateTimer: Timer
    private var isRepainting = false

    init {
        EventsDispatcher.register(this)

        createGui()

        onComponentsListUpdated()

        screenUpdateTimer = Timer((1000 / Config.fps)) {
            screenUpdateTimerStep()
        }
        screenUpdateTimer.start()
    }

    private fun createGui() {
        layout = BorderLayout()

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
        componentPanel.removeAll()

        canComponentPanels.forEach {
            componentPanel.add(it)
        }
        revalidate()
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