package nl.sajansen.canmoduleinterface.gui.menu

import nl.sajansen.canmoduleinterface.events.EventsDispatcher
import nl.sajansen.canmoduleinterface.events.SerialEventListener
import nl.sajansen.canmoduleinterface.gui.HotKeysMapping
import nl.sajansen.canmoduleinterface.hardware.CAN
import nl.sajansen.canmoduleinterface.serial.SerialConnectionState
import nl.sajansen.canmoduleinterface.utils.gui.addHotKeyMapping
import org.slf4j.LoggerFactory
import java.awt.Color
import javax.swing.BorderFactory
import javax.swing.JMenu
import javax.swing.JMenuItem

class ConnectionMenu : JMenu("Connection"), SerialEventListener {
    private val logger = LoggerFactory.getLogger(ConnectionMenu::class.java.name)

    private val connectItem = JMenuItem()

    init {
        EventsDispatcher.register(this)

        initGui()

        onSerialConnectionChanged(CAN.state)
    }

    private fun initGui() {
        popupMenu.border = BorderFactory.createLineBorder(Color(168, 168, 168))
        addHotKeyMapping(HotKeysMapping.Connect)

        val portItem = PortMenu()

        // Set alt keys
        portItem.addHotKeyMapping(HotKeysMapping.ChangePort)
        connectItem.addHotKeyMapping(HotKeysMapping.Connect)

        add(portItem)
        add(connectItem)

        connectItem.addActionListener { toggleConnection() }
    }

    private fun toggleConnection() {
        if (CAN.state != SerialConnectionState.NotConnected) {
            Thread { CAN.disconnect() }.start()
        } else {
            Thread { CAN.connect() }.start()
        }
    }

    override fun onSerialConnectionChanged(value: SerialConnectionState) {
        connectItem.text = if (CAN.state != SerialConnectionState.NotConnected) "Disconnect" else "Connect"
    }
}