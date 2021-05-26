package nl.sajansen.canmoduleinterface.serial

import com.fazecast.jSerialComm.SerialPort
import nl.sajansen.canmoduleinterface.ApplicationRuntimeSettings
import nl.sajansen.canmoduleinterface.config.Config
import nl.sajansen.canmoduleinterface.events.EventsDispatcher
import java.util.logging.Logger

open class SerialManager : SerialManagerInterface {
    private val logger = Logger.getLogger(SerialManager::class.java.name)

    @Volatile
    private var comPort: SerialPort? = null
    override fun getComPort() = comPort

    @Volatile
    var state: SerialConnectionState = SerialConnectionState.NotConnected
        set(value) {
            if (value != field) {
                logger.info("New serial connection state: ${value.name}")
            }

            field = value

            EventsDispatcher.onSerialConnectionChanged(value)
        }

    private val serialListener = SerialListener(this)

    fun connect(port: String, baudRate: Int): Boolean {
        if (ApplicationRuntimeSettings.virtualSerial) {
            logger.info("Connecting to virtual serial port")
            state = SerialConnectionState.Connecting
            Thread.sleep(1000)
            state = SerialConnectionState.Booting
            Thread.sleep(1000)
            serialListener.onDataReceived("${Config.serialStringBootDone}\n".toByteArray())
            return true
        }

        logger.info("Connecting to serial port '$port' with baud rate $baudRate")
        state = SerialConnectionState.Connecting

        comPort = SerialPort.getCommPorts().find { it.systemPortName == port }
        if (comPort == null) {
            logger.severe("Serial port '$port' not found")
            state = SerialConnectionState.NotConnected
            return false
        }

        comPort!!.baudRate = baudRate
        val connected = comPort!!.openPort()

        if (!connected) {
            logger.severe("Could not connect to serial port '$port'")
            state = SerialConnectionState.NotConnected
            return false
        }

        logger.info("Connected to serial port '$port' (${comPort?.descriptivePortName})")
        clearComPort()
        comPort!!.addDataListener(serialListener)

        state = SerialConnectionState.Booting
        return true
    }

    fun disconnect() {
        logger.info("Disconnecting hardware")
        if (!ApplicationRuntimeSettings.virtualSerial) {
            comPort?.closePort()
        }
        state = SerialConnectionState.NotConnected
        logger.info("Hardware device disconnected")
    }

    fun clearComPort() {
        logger.fine("Clearing com port buffer...")
        while (comPort!!.bytesAvailable() > 0) {
            val byteBuffer = ByteArray(comPort!!.bytesAvailable())
            comPort?.readBytes(byteBuffer, byteBuffer.size.toLong())
        }
        serialListener.clear()
        logger.fine("Com port buffer cleared")
    }

    override fun processSerialInput(data: List<String>) {
        if (Config.serialStringBootDone in data) {
            logger.info("Serial device is done booting")
            state = SerialConnectionState.Running
        }
    }

    fun send(data: String) = serialListener.send(data)
}