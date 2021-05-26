package nl.sajansen.canmoduleinterface.events

import nl.sajansen.canmoduleinterface.serial.SerialConnectionState

interface SerialEventListener {
    fun onSerialConnectionChanged(value: SerialConnectionState) {}
    fun onSerialDataReceived(data: List<String>) {}
    fun onSerialDataSend(data: String) {}
}