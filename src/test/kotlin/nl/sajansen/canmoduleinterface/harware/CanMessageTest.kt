package nl.sajansen.canmoduleinterface.harware

import nl.sajansen.canmoduleinterface.hardware.CanMessage
import kotlin.test.Test
import kotlin.test.assertEquals

class CanMessageTest {
    @Test
    fun `test stringify message`() {
        val message = CanMessage(1, byteArrayOf(18, 0, 49))
        assertEquals("CanMessage(id=1, data=[18, 0, 49])", message.toString())
    }
}