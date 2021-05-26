package nl.sajansen.canmoduleinterface.serial

import com.fazecast.jSerialComm.SerialPort

interface SerialInteraction {
    var state: SerialConnectionState

    fun getComPort(): SerialPort?
    fun connect(deviceName: String, baudRate: Int): Boolean
    fun disconnect() {}
    fun processSerialInput(data: List<String>)
}