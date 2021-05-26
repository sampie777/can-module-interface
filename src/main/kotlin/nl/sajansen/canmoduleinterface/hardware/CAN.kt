package nl.sajansen.canmoduleinterface.hardware

import nl.sajansen.canmoduleinterface.config.Config
import nl.sajansen.canmoduleinterface.events.SerialEventListener
import nl.sajansen.canmoduleinterface.serial.SerialConnectionState
import nl.sajansen.canmoduleinterface.serial.SerialManager
import org.slf4j.LoggerFactory
import java.nio.BufferOverflowException
import java.nio.ByteBuffer

object CAN : SerialManager(), SerialEventListener {
    private val logger = LoggerFactory.getLogger(CAN::class.java)

    val components = arrayListOf<CanComponent>(
        CanComponent(0x000),
        CanComponent(0x001),
        CanComponent(0x002),
        CanComponent(0x003),
    )

    var bootMessageProcessed = false

    fun processCanMessage(message: CanMessage) {
        val component = components.find { it.id == message.id }
        if (component == null) {
            logger.warn("Unknown ID received: ${message.id}")
            return
        }

        try {
            component.update(messageDataToValue(message))
        } catch (e: BufferOverflowException) {
            logger.warn("CAN message has to much data bytes: $message")
            e.printStackTrace()
        }
    }

    fun messageDataToValue(message: CanMessage): Long {
        val buffer = ByteBuffer.allocate(Long.SIZE_BYTES)

        // Fill empty places in buffer which will not be filled by message.data
        for (i in message.data.size until buffer.limit()) {
            buffer.put(0)
        }

        buffer.put(message.data)
        buffer.flip()
        return buffer.long
    }

    fun connect() = connect(Config.serialComPort, Config.serialComBaudRate)

    override fun onSerialDataReceived(data: List<String>) {
        if (state != SerialConnectionState.Running) {
            return
        }

        // Only process messages after boot-done message
        if (!bootMessageProcessed && Config.serialStringBootDone in data) {
            logger.info("Boot-done message found")
            val index = data.indexOf(Config.serialStringBootDone)
            bootMessageProcessed = true

            if (index != data.size - 1) {
                logger.info("Processing remainder of data array")
                onSerialDataReceived(data.subList(index + 1, data.size))
            }
            return
        }

        data.mapNotNull { createCanMessageFromString(it) }
            .forEach(CAN::processCanMessage)
    }

    fun createCanMessageFromString(text: String): CanMessage? {
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