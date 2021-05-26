package nl.sajansen.canmoduleinterface.gui.can.components


import nl.sajansen.canmoduleinterface.config.Config
import nl.sajansen.canmoduleinterface.gui.Theme
import nl.sajansen.canmoduleinterface.hardware.CanComponent
import org.slf4j.LoggerFactory
import java.awt.*
import java.util.*
import javax.swing.BorderFactory
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.border.EmptyBorder

class CanComponentPanel(private val component: CanComponent) : JPanel() {
    private val logger = LoggerFactory.getLogger(CanComponentPanel::class.java.name)

    private val canvas = CanComponentCanvas(component)

    init {
        createGui()
    }

    private fun createGui() {
        layout = BorderLayout()
        border = BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 2, Color(230, 230, 230)),
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1)
        )

        val idLabel = JLabel(String.format("0x%02X", component.id))
        idLabel.font = Theme.normalFont
        idLabel.isOpaque = true
        idLabel.background = Color.WHITE
        idLabel.border = BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 2, Color(225, 225, 225)),
            EmptyBorder(5, 15, 5, 15)
        )
        add(idLabel, BorderLayout.LINE_START)

        canvas.addActionListener { switchType() }
        add(canvas, BorderLayout.CENTER)
    }

    private fun switchType() {
        val types = CanComponentCanvas.Type.values()

        var nextTypeIndex = types.indexOf(canvas.type) + 1
        if (nextTypeIndex >= types.size) {
            nextTypeIndex = 0
        }

        canvas.type = types[nextTypeIndex]
    }

    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)

        if (component.lastUpdateTime.time + Config.inactiveTimeout >= Date().time) {
            return
        }

        // Blur component a bit to not attract the focus
        val g2 = g as Graphics2D
        g2.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f)
    }
}