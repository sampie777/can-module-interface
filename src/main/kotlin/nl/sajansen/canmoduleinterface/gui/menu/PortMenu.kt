package nl.sajansen.canmoduleinterface.gui.menu

import com.fazecast.jSerialComm.SerialPort
import nl.sajansen.canmoduleinterface.config.Config
import org.slf4j.LoggerFactory
import java.awt.Color
import javax.swing.BorderFactory
import javax.swing.JCheckBoxMenuItem
import javax.swing.JMenu
import javax.swing.JMenuItem

class PortMenu : JMenu("Port") {
    private val logger = LoggerFactory.getLogger(PortMenu::class.java)

    private val scanItem = JMenuItem("Scan")

    init {
        initGui()
        updateSerialDevices()
    }

    private fun initGui() {
        popupMenu.border = BorderFactory.createLineBorder(Color(168, 168, 168))

        scanItem.addActionListener { updateSerialDevices() }
    }

    private fun updateSerialDevices() {
        removeAll()
        SerialPort.getCommPorts().forEach { port ->
            val deviceItem = JCheckBoxMenuItem("[${port.systemPortName}]   ${port.descriptivePortName}")
            deviceItem.state = Config.serialComPort == port.systemPortName
            deviceItem.addItemListener { setActiveSerialPort(port) }

            add(deviceItem)
        }

        addSeparator()
        add(scanItem)
    }

    private fun setActiveSerialPort(port: SerialPort) {
        logger.info("Setting new hardware device to: [${port.systemPortName}] ${port.descriptivePortName}")
        Config.serialComPort = port.systemPortName
        Config.save()

        updateSerialDevices()
    }
}