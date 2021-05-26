package nl.sajansen.canmoduleinterface.gui.mainFrame

import nl.sajansen.canmoduleinterface.events.EventsDispatcher
import nl.sajansen.canmoduleinterface.events.GuiEventListener
import nl.sajansen.canmoduleinterface.gui.can.StatusPanel
import org.slf4j.LoggerFactory
import java.awt.BorderLayout
import javax.swing.JPanel


class MainFramePanel : JPanel(), GuiEventListener {
    private val logger = LoggerFactory.getLogger(MainFramePanel::class.java.name)

    init {
        EventsDispatcher.register(this)

        createGui()

        refreshNotifications()
    }

    private fun createGui() {
        border = null
        layout = BorderLayout(10, 10)

        add(StatusPanel(), BorderLayout.CENTER)
        add(StatusBar(), BorderLayout.PAGE_END)
    }

    override fun removeNotify() {
        super.removeNotify()
        EventsDispatcher.unregister(this)
    }

}