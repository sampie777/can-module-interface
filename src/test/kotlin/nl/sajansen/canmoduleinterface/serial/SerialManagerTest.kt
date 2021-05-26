package nl.sajansen.canmoduleinterface.serial

import nl.sajansen.canmoduleinterface.config.Config
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class SerialManagerTest {
    @Test
    fun `test processing boot finished command changes state to running`() {
        val manager = SerialManager()
        assertEquals(SerialConnectionState.NotConnected, manager.state)

        manager.processSerialInput(listOf(Config.serialStringBootDone))

        assertEquals(SerialConnectionState.Running, manager.state)
    }

    @Test
    fun `test processing boot finished command doesn't change state when state is already running`() {
        val manager = SerialManager()
        assertEquals(SerialConnectionState.NotConnected, manager.state)

        manager.processSerialInput(listOf(Config.serialStringBootDone))
        manager.processSerialInput(listOf(Config.serialStringBootDone))

        assertEquals(SerialConnectionState.Running, manager.state)
    }

    @Test
    fun `test processing command before boot is finished doesn't change state`() {
        val manager = SerialManager()
        assertEquals(SerialConnectionState.NotConnected, manager.state)

        manager.processSerialInput(listOf(Config.serialStringBootDone + "xxx"))

        assertEquals(SerialConnectionState.NotConnected, manager.state)
    }

    @Test
    fun `test connecting to unknown port returns false and sets state to disconnected`() {
        val manager = SerialManager()
        manager.state = SerialConnectionState.Running

        assertFalse(manager.connect("__", 9600))

        assertEquals(SerialConnectionState.NotConnected, manager.state)
    }
}