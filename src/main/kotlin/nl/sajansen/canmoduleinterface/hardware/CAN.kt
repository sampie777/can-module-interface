package nl.sajansen.canmoduleinterface.hardware

import nl.sajansen.canmoduleinterface.config.Config
import nl.sajansen.canmoduleinterface.events.SerialEventListener
import nl.sajansen.canmoduleinterface.serial.SerialConnectionState
import nl.sajansen.canmoduleinterface.serial.SerialManager
import org.slf4j.LoggerFactory
import java.nio.ByteBuffer

object CAN : SerialManager(), SerialEventListener {
    private val logger = LoggerFactory.getLogger(CAN::class.java)

    val components = arrayListOf<CanComponent>(
        CanComponent(0x000),
        CanComponent(0x001),
        CanComponent(0x002),
        CanComponent(0x003),
    )

    private var bootMessageProcessed = false

    fun processCanMessage(message: CanMessage) {
        val component = components.find { it.id == message.id }
        if (component == null) {
            logger.warn("Unknown ID received: ${message.id}")
            return
        }

        component.update(messageDataToValue(message))
    }

    private fun messageDataToValue(message: CanMessage): Long {
        val buffer = ByteBuffer.allocate(Long.SIZE_BYTES)
        buffer.put(message.data)
        if (Config.flipBytesToLong) {
            buffer.flip()
        }
        return buffer.long
    }

    fun connect() = connect(Config.serialComPort, Config.serialComBaudRate)

    override fun onSerialDataReceived(data: List<String>) {
        if (state != SerialConnectionState.Running) {
            return
        }

        // Only process messages after boot-done message
        if (!bootMessageProcessed && Config.serialStringBootDone in data) {
            val index = data.indexOf(Config.serialStringBootDone)
            bootMessageProcessed = true

            if (index != data.size - 1) {
                onSerialDataReceived(data.subList(index + 1, data.size - 1))
            }
            return
        }

        data.mapNotNull { createCanMessageFromString(it) }
            .forEach(CAN::processCanMessage)
    }

    private fun createCanMessageFromString(text: String): CanMessage? {
        val bytes = text.toByteArray()
        if (bytes.isEmpty()) {
            logger.warn("Empty data array received")
            return null
        }

        val id = bytes[0].toInt()
        val data = ByteArray(bytes.size - 1)

        for (i in data.indices) {
            data[i] = bytes[i + 1]
        }

        return CanMessage(id, data)
    }
}