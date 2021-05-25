package nl.sajansen.canmoduleinterface.gui.mainFrame

import nl.sajansen.canmoduleinterface.gui.GUI
import nl.sajansen.canmoduleinterface.gui.Refreshable
import org.slf4j.LoggerFactory
import java.awt.BorderLayout
import javax.swing.JPanel


class MainFramePanel : JPanel(), Refreshable {
    private val logger = LoggerFactory.getLogger(MainFramePanel::class.java.name)

    init {
        GUI.register(this)

        createGui()

        refreshNotifications()
    }

    private fun createGui() {
        border = null
        layout = BorderLayout(10, 10)
    }

    override fun removeNotify() {
        super.removeNotify()
        GUI.unregister(this)
    }

}