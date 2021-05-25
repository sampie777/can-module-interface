package nl.sajansen.canmoduleinterface.hardware

import nl.sajansen.canmoduleinterface.config.Config
import org.slf4j.LoggerFactory
import java.nio.ByteBuffer

object CAN {
    private val logger = LoggerFactory.getLogger(CAN::class.java)

    val components = arrayListOf<CanComponent>(
        CanComponent(0x000),
        CanComponent(0x001),
        CanComponent(0x002),
        CanComponent(0x003),
    )

    fun processCanMessage(message: CanMessage) {
        val component = components.find { it.id == message.id }
        if (component == null) {
            logger.warn("Unknown ID received: ${message.id}")
            return
        }

        component.update(messageDataToValue(message))
    }

    fun messageDataToValue(message: CanMessage): Long {
        val buffer = ByteBuffer.allocate(Long.SIZE_BYTES)
        buffer.put(message.data)
        if (Config.flipBytesToLong) {
            buffer.flip()
        }
        return buffer.long
    }
}