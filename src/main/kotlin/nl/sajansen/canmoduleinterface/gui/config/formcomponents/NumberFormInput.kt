package nl.sajansen.canmoduleinterface.gui.config.formcomponents

import nl.sajansen.canmoduleinterface.config.Config
import nl.sajansen.canmoduleinterface.gui.Theme
import org.slf4j.LoggerFactory
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import javax.swing.*

class NumberFormInput<T : Number>(
    override val key: String,
    private val labelText: String,
    private val min: T?,
    private val max: T?,
    private val step: T?,
) : FormInput {
    companion object {
        private val logger = LoggerFactory.getLogger(NumberFormInput::class.java.name)
    }

    private val input = JSpinner()

    @Suppress("UNCHECKED_CAST")
    override fun component(): Component {
        val configValue: T? = Config.get(key) as? T

        val label = JLabel(labelText)
        label.font = Theme.normalFont

        input.model = SpinnerNumberModel(configValue, min as? Comparable<T>, max as? Comparable<T>, step)
        input.preferredSize = Dimension(150, 20)
        input.border = BorderFactory.createLineBorder(Color(168, 168, 168))
        input.font = Theme.normalFont

        val panel = JPanel()
        panel.layout = BorderLayout(10, 10)
        panel.add(label, BorderLayout.LINE_START)
        panel.add(input, BorderLayout.LINE_END)
        return panel
    }

    override fun validate(): List<String> {
        val errors = ArrayList<String>()

        if (min != null && value().toDouble() < min.toDouble()) {
            errors.add("Value for '$labelText' is to small")
        }
        if (max != null && value().toDouble() > max.toDouble()) {
            errors.add("Value for '$labelText' is to large")
        }

        return errors
    }

    override fun save() {
        Config.set(key, value())
    }

    @Suppress("UNCHECKED_CAST")
    override fun value(): T {
        return input.value as T
    }
}