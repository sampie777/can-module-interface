package nl.sajansen.canmoduleinterface.serial

import com.fazecast.jSerialComm.SerialPort
import com.fazecast.jSerialComm.SerialPortEvent
import nl.sajansen.canmoduleinterface.ApplicationRuntimeSettings
import nl.sajansen.canmoduleinterface.events.EventsDispatcher
import nl.sajansen.canmoduleinterface.events.SerialEventListener
import nl.sajansen.canmoduleinterface.mocks.SerialManagerMock
import kotlin.test.*

class SerialListenerTest {
    private val manager = SerialManagerMock()
    private val serialListener = SerialListener(manager)

    // Get random available com port. If no com ports are available, these tests can't be run.
    private val port = SerialPort.getCommPorts()[0]
    val event = SerialPortEvent(port, SerialPort.LISTENING_EVENT_DATA_RECEIVED, "test\n".toByteArray())

    @BeforeTest
    fun before() {
        ApplicationRuntimeSettings.virtualSerial = true
        manager.data = emptyList()
        serialListener.clear()

        EventsDispatcher.registeredComponents().forEach { EventsDispatcher.unregister(it) }
    }

    @Test
    fun `test processing serial event bubbles through serial manager`() {
        // When
        serialListener.serialEvent(event)

        assertEquals("test", manager.data[0])
    }

    @Test
    fun `test processing serial event bubbles through event dispatcher`() {
        val listener = object : SerialEventListener {
            var data = listOf<String>()
            override fun onSerialDataReceived(data: List<String>) {
                this.data = data
            }
        }
        EventsDispatcher.register(listener)

        // When
        serialListener.serialEvent(event)

        assertEquals("test", listener.data[0])
    }

    @Test
    fun `test serial listener only bubbles data which are complete`() {
        val event = SerialPortEvent(port, SerialPort.LISTENING_EVENT_DATA_RECEIVED, "complete1\ncomplete2\nincomplete".toByteArray())

        // When
        serialListener.serialEvent(event)

        assertEquals(2, manager.data.size)
        assertEquals("complete1", manager.data[0])
        assertEquals("complete2", manager.data[1])
    }

    @Test
    fun `test sending serial data bubbles through event dispatcher`() {
        val listener = object : SerialEventListener {
            var data = ""
            override fun onSerialDataSend(data: String) {
                this.data = data
            }
        }
        EventsDispatcher.register(listener)

        // When
        assertTrue(serialListener.send("test"))

        assertEquals("test", listener.data)
    }

    @Test
    fun `test sending serial data with no com port stops the operation without exceptions`() {
        val listener = object : SerialEventListener {
            var data = "1"
            override fun onSerialDataSend(data: String) {
                this.data = data
            }
        }
        EventsDispatcher.register(listener)
        manager.useComPort = null
        ApplicationRuntimeSettings.virtualSerial = false

        // When
        assertFalse(serialListener.send("test"))

        assertEquals("1", listener.data)
    }
}