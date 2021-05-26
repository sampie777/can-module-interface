package nl.sajansen.canmoduleinterface.serial


import com.fazecast.jSerialComm.SerialPort
import com.fazecast.jSerialComm.SerialPortDataListener
import com.fazecast.jSerialComm.SerialPortEvent
import nl.sajansen.canmoduleinterface.ApplicationRuntimeSettings
import nl.sajansen.canmoduleinterface.events.EventsDispatcher
import java.util.logging.Logger

class SerialListener(private val manager: SerialManager) : SerialPortDataListener {
    private val logger = Logger.getLogger(SerialListener::class.java.name)

    private var keepAllMessages: Boolean = false
    private var currentDataLine: String = ""
    private val receivedDataLines = ArrayList<String>()

    override fun getListeningEvents(): Int {
        return SerialPort.LISTENING_EVENT_DATA_RECEIVED
    }

    override fun serialEvent(event: SerialPortEvent) {
        if (event.eventType != SerialPort.LISTENING_EVENT_DATA_RECEIVED) {
            logger.warning("Got invalid event type: ${event.eventType}")
            return
        }

        onDataReceived(event.receivedData)
    }

    fun onDataReceived(data: ByteArray) {
        currentDataLine += String(data)
        val messages = currentDataLine
            .split("\n")
            .map { it.trim('\r') }

        val terminatedMessages = messages.subList(0, messages.size - 1)
        currentDataLine = messages.last()

        if (keepAllMessages) {
            receivedDataLines.addAll(terminatedMessages)
        }
        terminatedMessages.forEach {
            logger.fine("Serial data: $it")
        }

        manager.processSerialInput(terminatedMessages)
        EventsDispatcher.onSerialDataReceived(terminatedMessages)
    }

    fun send(data: String) {
        logger.fine("Sending data to serial device: $data")
        if (ApplicationRuntimeSettings.virtualSerial) {
            EventsDispatcher.onSerialDataSend(data)
            return
        }

        if (manager.getComPort() == null) {
            logger.warning("Serial device unconnected, cannot send data")
            return
        }

        val dataBytes = data.toByteArray()
        val writtenBytes = manager.getComPort()?.writeBytes(dataBytes, dataBytes.size.toLong())

        if (writtenBytes != dataBytes.size) {
            logger.warning("Not all bytes were sent. Only $writtenBytes out of ${dataBytes.size}")
        }

        EventsDispatcher.onSerialDataSend(data)
    }

    fun clear() {
        logger.fine("Clearing serial data buffer")
        receivedDataLines.clear()
        currentDataLine = ""
    }
}