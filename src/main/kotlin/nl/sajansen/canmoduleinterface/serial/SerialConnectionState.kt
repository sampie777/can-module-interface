package nl.sajansen.canmoduleinterface.serial

enum class SerialConnectionState {
    NotConnected,
    Connecting,
    Booting,
    Running,
}