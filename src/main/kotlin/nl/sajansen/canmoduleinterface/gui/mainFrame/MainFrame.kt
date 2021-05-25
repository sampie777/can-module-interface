package nl.sajansen.canmoduleinterface.gui.mainFrame

import nl.sajansen.canmoduleinterface.ApplicationInfo
import nl.sajansen.canmoduleinterface.config.Config
import nl.sajansen.canmoduleinterface.gui.menu.MenuBar
import nl.sajansen.canmoduleinterface.utils.gui.loadIcon
import org.slf4j.LoggerFactory
import javax.swing.JFrame


class MainFrame : JFrame() {
    private val logger = LoggerFactory.getLogger(MainFrame::class.java.name)

    companion object {
        private var instance: MainFrame? = null
        fun getInstance() = instance

        fun create(): MainFrame = MainFrame()

        fun createAndShow(): MainFrame {
            val frame = create()
            frame.isVisible = true
            return frame
        }
    }

    init {
        instance = this

        addWindowListener(MainFrameWindowAdapter(this))

        initGUI()
    }

    private fun initGUI() {
        setSize(580, 450)
        setLocationRelativeTo(null)

        add(MainFramePanel())

        isAlwaysOnTop = Config.mainWindowAlwaysOnTop

        jMenuBar = MenuBar()
        defaultCloseOperation = EXIT_ON_CLOSE
        iconImage = loadIcon("images/icon-512.png")
        title = ApplicationInfo.name
    }

    fun saveWindowPosition() {
    }
}