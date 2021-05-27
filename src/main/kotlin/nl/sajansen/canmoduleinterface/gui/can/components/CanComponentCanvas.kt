package nl.sajansen.canmoduleinterface.gui.can.components


import nl.sajansen.canmoduleinterface.hardware.CanComponent
import nl.sajansen.canmoduleinterface.hardware.CanComponentUtils
import nl.sajansen.canmoduleinterface.utils.gui.createGraphics
import nl.sajansen.canmoduleinterface.utils.gui.getNumericFontHeight
import nl.sajansen.canmoduleinterface.utils.gui.setDefaultRenderingHints
import org.slf4j.LoggerFactory
import java.awt.*
import java.awt.image.BufferedImage
import javax.swing.JButton

class CanComponentCanvas(private val component: CanComponent) : JButton() {
    companion object {
        private val logger = LoggerFactory.getLogger(CanComponentCanvas::class.java.name)
    }

    enum class Type {
        Analog,
        Digital,
    }

    var type = Type.Analog

    private val bitWidth = 20
    private val bitHeight = 20
    private val bitMargin = 10

    init {
        createGui()
    }

    private fun createGui() {
        preferredSize = Dimension(250, 40)
        minimumSize = preferredSize
        isBorderPainted = false
        isContentAreaFilled = false
        isFocusPainted = false
    }

    override fun paint(g: Graphics?) {
        super.paint(g)
        val g2 = g as Graphics2D
        setDefaultRenderingHints(g2)

        when (type) {
            Type.Digital -> paintDigital(g2)
            else -> paintAnalog(g2)
        }
    }

    private fun paintAnalog(g2: Graphics2D) {
        g2.stroke = BasicStroke(1F)
        g2.font = Font("Dialog", Font.PLAIN, 16)

        // Clear panel
        g2.color = background
        g2.fill(Rectangle(0, 0, width, height))

        // Draw value
        val percentage = CanComponentUtils.calculatePercentage(component)
        val fillRectangle = Rectangle(0, 0, (percentage * (width - 1)).toInt(), height - 1)
        g2.color = Color(42, 208, 42)
        g2.fill(fillRectangle)
        g2.color = background
        g2.stroke = BasicStroke(5F)
        g2.draw(fillRectangle)

        val text = component.value.toString()
        val fontMetrics: FontMetrics = g2.fontMetrics
        val fontHeight = getNumericFontHeight(fontMetrics)
        val fontWidth = fontMetrics.stringWidth(text)
        g2.stroke = BasicStroke(1F)
        g2.color = Color(0, 100, 0)
        g2.drawString(text, ((width - fontWidth).toDouble() / 2).toInt(), ((height + fontHeight) / 2).toInt())

        updateComponentSize()
    }

    private fun paintDigital(g2: Graphics2D) {
        g2.stroke = BasicStroke(1F)
        g2.font = Font("Dialog", Font.PLAIN, 16)

        // Clear panel
        g2.color = background
        g2.fill(Rectangle(0, 0, width, height))

        // Draw found bit values
        val activeBits = CanComponentUtils.getActiveBitValues(component).entries
        activeBits
            .forEachIndexed { i, bit ->
                val bitImage = createBitDisplay(bit.key, bit.value, bitWidth, bitHeight)
                g2.drawImage(bitImage, null, bitMargin + i * (bitWidth + bitMargin), ((height - bitHeight).toDouble() / 2).toInt())
            }

        updateComponentSize(bitMargin + activeBits.size * (bitWidth + bitMargin))
    }

    private fun updateComponentSize(toWidth: Int = minimumSize.width) {
        if (width == toWidth) {
            return
        }

        if (toWidth <= minimumSize.width) {
            preferredSize = minimumSize
        } else if (toWidth > width) {
            preferredSize = Dimension(toWidth, height)
        }

        if (preferredSize.width != width) {
            revalidate()
        }
    }

    private fun createBitDisplay(index: Int, value: Boolean, width: Int = 20, height: Int = 20): BufferedImage {
        val (bufferedImage: BufferedImage, g2: Graphics2D) = createGraphics(width, height)

        // Draw value
        val rectangle = Rectangle(0, 0, width - 1, height - 1)
        g2.color = if (value) Color(255, 100, 100) else background
        g2.fill(rectangle)
        g2.color = background
        g2.stroke = BasicStroke(3F)
        g2.draw(rectangle)

        // Draw border
        g2.color = Color(180, 180, 180)
        g2.stroke = BasicStroke(1F)
        g2.draw(rectangle)

        // Draw index
        val text = index.toString()
        val fontMetrics: FontMetrics = g2.fontMetrics
        val fontHeight = getNumericFontHeight(fontMetrics)
        val fontWidth = fontMetrics.stringWidth(text)
        g2.font = Font("Dialog", Font.PLAIN, 12)
        g2.stroke = BasicStroke(1F)
        g2.color = Color(50, 0, 0)
        g2.drawString(text, ((width - fontWidth).toDouble() / 2).toInt(), ((height + fontHeight) / 2).toInt())

        g2.dispose()
        return bufferedImage
    }
}