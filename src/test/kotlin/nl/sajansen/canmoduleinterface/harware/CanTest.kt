package nl.sajansen.canmoduleinterface.harware

import nl.sajansen.canmoduleinterface.config.Config
import nl.sajansen.canmoduleinterface.hardware.CAN
import nl.sajansen.canmoduleinterface.hardware.CanComponent
import nl.sajansen.canmoduleinterface.hardware.CanMessage
import nl.sajansen.canmoduleinterface.serial.SerialConnectionState
import java.nio.BufferOverflowException
import kotlin.test.*

class CanTest {

    @BeforeTest
    fun before() {
        CAN.components.clear()
        CAN.components.add(CanComponent(0))
        CAN.state = SerialConnectionState.Running
        CAN.bootMessageProcessed = false
    }

    @Test
    fun `test string to CAN message`() {
        val id = byteArrayOf(0x07)
        val data = byteArrayOf(0x01, 0x012, 0x77)
        val string = String(id + data)

        val message = CAN.createCanMessageFromString(string)

        assertNotNull(message)
        assertEquals(7, message.id)
        assertContentEquals(data, message.data)
    }

    @Test
    fun `test string with id but no data to CAN message`() {
        val id = byteArrayOf(0x07)
        val string = String(id)

        val message = CAN.createCanMessageFromString(string)

        assertNotNull(message)
        assertEquals(7, message.id)
        assertContentEquals(byteArrayOf(), message.data)
    }

    @Test
    fun `test empty string to CAN message returns null`() {
        val string = ""

        val message = CAN.createCanMessageFromString(string)

        assertNull(message)
    }

    @Test
    fun `test don't process data before boot is done`() {
        CAN.components[0].value = 0
        val id1 = byteArrayOf(CAN.components[0].id.toByte())
        val data1 = byteArrayOf(0x00, 0x00, 0x7)
        val string1 = String(id1 + data1)
        val data = listOf(string1, Config.serialStringBootDone)

        CAN.onSerialDataReceived(data)

        assertEquals(0, CAN.components[0].value)
    }

    @Test
    fun `test process data after boot is done`() {
        val id1 = byteArrayOf(CAN.components[0].id.toByte())
        val data1 = byteArrayOf(0x00, 0x00, 18)
        val string1 = String(id1 + data1)

        CAN.onSerialDataReceived(listOf(Config.serialStringBootDone))
        CAN.onSerialDataReceived(listOf(string1))

        assertEquals(18, CAN.components[0].value)
    }

    @Test
    fun `test process data after boot is done within same message frame`() {
        val id1 = byteArrayOf(CAN.components[0].id.toByte())
        val data1 = byteArrayOf(0x00, 0x00, 18)
        val string1 = String(id1 + data1)
        val data = listOf(Config.serialStringBootDone, string1)

        CAN.onSerialDataReceived(data)

        assertEquals(18, CAN.components[0].value)
    }

    @Test
    fun `test processing CAN message updates corresponding component`() {
        val message = CanMessage(0, byteArrayOf(0x00, 0x00, 18))

        CAN.processCanMessage(message)

        assertEquals(18, CAN.components[0].value)
    }

    @Test
    fun `test processing CAN message with no matching component does nothing`() {
        val message = CanMessage(99, byteArrayOf(0x00, 0x00, 18))
        CAN.components[0].value = 0

        CAN.processCanMessage(message)

        assertEquals(0, CAN.components[0].value)
    }

    @Test
    fun `test processing CAN message with overflowing data does nothing`() {
        val message = CanMessage(0, byteArrayOf(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x12))
        CAN.components[0].value = 0

        CAN.processCanMessage(message)

        assertEquals(0, CAN.components[0].value)
    }

    @Test
    fun `test converting can message data to long`() {
        println((-0x01).toLong().toUByte())
        assertEquals(0, CAN.messageDataToValue(CanMessage(0, byteArrayOf(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00))))
        assertEquals(18, CAN.messageDataToValue(CanMessage(0, byteArrayOf(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 18))))
        assertEquals(-1, CAN.messageDataToValue(CanMessage(0, byteArrayOf(-0x01, -0x01, -0x01, -0x01, -0x01, -0x01, -0x01, -0x01))))
        assertEquals(18, CAN.messageDataToValue(CanMessage(0, byteArrayOf(0x00, 0x00, 18))))
        assertEquals(18, CAN.messageDataToValue(CanMessage(0, byteArrayOf(18))))
        assertEquals(0, CAN.messageDataToValue(CanMessage(0, byteArrayOf())))

        assertFailsWith(BufferOverflowException::class) {
            CAN.messageDataToValue(CanMessage(0, byteArrayOf(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x12)))
        }
    }
}