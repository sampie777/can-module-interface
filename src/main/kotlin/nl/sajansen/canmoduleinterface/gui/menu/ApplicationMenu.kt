package nl.sajansen.canmoduleinterface.gui.menu

import nl.sajansen.canmoduleinterface.exitApplication
import nl.sajansen.canmoduleinterface.gui.HotKeysMapping
import nl.sajansen.canmoduleinterface.gui.config.ConfigFrame
import nl.sajansen.canmoduleinterface.utils.gui.addHotKeyMapping
import nl.sajansen.canmoduleinterface.utils.gui.getMainFrameComponent
import org.slf4j.LoggerFactory
import java.awt.Color
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import javax.swing.BorderFactory
import javax.swing.JMenu
import javax.swing.JMenuItem
import javax.swing.KeyStroke

class ApplicationMenu : JMenu("Application") {
    private val logger = LoggerFactory.getLogger(ApplicationMenu::class.java.name)

    init {
        initGui()
    }

    private fun initGui() {
        popupMenu.border = BorderFactory.createLineBorder(Color(168, 168, 168))
        addHotKeyMapping(HotKeysMapping.ApplicationMenu)

        val settingsItem = JMenuItem("Settings")
        val infoItem = JMenuItem("Info")
        val quitItem = JMenuItem("Quit")

        // Set alt keys
        settingsItem.addHotKeyMapping(HotKeysMapping.ShowConfig)
        infoItem.addHotKeyMapping(HotKeysMapping.ShowApplicationInfo)
        quitItem.addHotKeyMapping(HotKeysMapping.QuitApplication)
        quitItem.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK)

        add(settingsItem)
        addSeparator()
        add(infoItem)
        add(quitItem)

        settingsItem.addActionListener { ConfigFrame(getMainFrameComponent(this)) }
        infoItem.addActionListener { InfoFrame.createAndShow(getMainFrameComponent(this)) }
        quitItem.addActionListener { exitApplication() }
    }
}