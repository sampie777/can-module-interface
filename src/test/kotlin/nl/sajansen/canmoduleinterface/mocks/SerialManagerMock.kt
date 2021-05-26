package nl.sajansen.canmoduleinterface.mocks

import com.fazecast.jSerialComm.SerialPort
import nl.sajansen.canmoduleinterface.serial.SerialManagerInterface

class SerialManagerMock : SerialManagerInterface {
    var data = emptyList<String>()
    var useComPort: SerialPort? = null

    override fun getComPort(): SerialPort? {
        return useComPort
    }

    override fun processSerialInput(data: List<String>) {
        this.data = data
    }
}