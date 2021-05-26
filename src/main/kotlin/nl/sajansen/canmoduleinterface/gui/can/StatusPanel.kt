package nl.sajansen.canmoduleinterface.gui.can

import nl.sajansen.canmoduleinterface.config.Config
import nl.sajansen.canmoduleinterface.gui.can.components.CanComponentPanel
import nl.sajansen.canmoduleinterface.hardware.CAN
import org.slf4j.LoggerFactory
import java.awt.FlowLayout
import java.awt.Graphics
import javax.swing.JPanel
import javax.swing.Timer

class StatusPanel : JPanel() {
    private val logger = LoggerFactory.getLogger(StatusPanel::class.java.name)

    private val screenUpdateTimer: Timer
    private var isRepainting = false

    init {
        createGui()

        screenUpdateTimer = Timer((1000 / Config.fps)) {
            screenUpdateTimerStep()
        }
        screenUpdateTimer.start()
    }

    private fun screenUpdateTimerStep() {
        if (isRepainting) {
            logger.debug("Skipping paint update: still updating")
            return
        }

        repaint()
    }

    private fun createGui() {
        layout = FlowLayout(FlowLayout.LEADING, 10, 10)

        CAN.components.forEach {
            add(CanComponentPanel(it))
        }
    }

    override fun paint(g: Graphics) {
        isRepainting = true
        super.paint(g)
        isRepainting = false
    }
}