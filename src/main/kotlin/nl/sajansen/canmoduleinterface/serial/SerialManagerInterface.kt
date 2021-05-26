package nl.sajansen.canmoduleinterface.serial

import com.fazecast.jSerialComm.SerialPort

interface SerialManagerInterface {
    fun getComPort(): SerialPort?
    fun processSerialInput(data: List<String>)
}