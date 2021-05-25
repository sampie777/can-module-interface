package nl.sajansen.canmoduleinterface.gui.mainFrame

import nl.sajansen.canmoduleinterface.exitApplication
import nl.sajansen.canmoduleinterface.gui.GUI
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent


class MainFrameWindowAdapter(private val frame: MainFrame) : WindowAdapter() {
    override fun windowClosing(winEvt: WindowEvent) {
        exitApplication()
    }

    override fun windowActivated(e: WindowEvent?) {
        super.windowActivated(e)
        GUI.currentFrame = frame
    }
}