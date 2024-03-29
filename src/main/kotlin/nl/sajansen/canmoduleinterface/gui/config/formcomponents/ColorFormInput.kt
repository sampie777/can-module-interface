package nl.sajansen.canmoduleinterface.gui.config.formcomponents

import nl.sajansen.canmoduleinterface.config.Config
import nl.sajansen.canmoduleinterface.gui.Theme
import org.slf4j.LoggerFactory
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import javax.swing.JButton
import javax.swing.JColorChooser
import javax.swing.JLabel
import javax.swing.JPanel

class ColorFormInput(
    override val key: String,
    private val labelText: String
) : FormInput {
    companion object {
        private val logger = LoggerFactory.getLogger(ColorFormInput::class.java.name)
    }

    private lateinit var color: Color
    private val button = JButton()
    private val panel = JPanel()

    override fun component(): Component {
        color = Config.get(key) as? Color ?: Color.BLACK

        val label = JLabel(labelText)
        label.font = Theme.normalFont

        button.text = "Choose"
        button.background = color
        button.preferredSize = Dimension(100, 30)
        button.addActionListener {
            chooseColor()
        }

        panel.layout = BorderLayout(10, 10)
        panel.add(label, BorderLayout.LINE_START)
        panel.add(button, BorderLayout.LINE_END)
        return panel
    }

    private fun chooseColor() {
        color = JColorChooser.showDialog(
            this.panel,
            "Choose Color",
            color
        )
            ?: return
        button.background = color
    }

    override fun validate(): List<String> {
        return ArrayList()
    }

    override fun save() {
        Config.set(key, value())
    }

    override fun value(): Color {
        return color
    }
}