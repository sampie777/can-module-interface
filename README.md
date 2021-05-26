# CAN module interface

_This project aims to receive, process, and visualize data send from an CAN bus reader._

## Serial connection

The serial connection must be manually started (connected) and terminated (disconnected).  

The baud rate and COM port of the serial connection can be configured in the Settings.

### Serial device 

The serial device is required to:

- Send the string "Ready." when boot finished and is ready to send out data.
- Send the data in the following form as raw bytes: `id> <data0> <data...>`. 
- All data messages must be terminated with the newline (`\n`) character.
- No other forms of message may be sent after boot finished. 

## Development

Serial messages can be received by implementing the `SerialEventListener` interface.

Serial messages can be sent using the `CAN` instance. 

Serial connection is managed through the `CAN` instance (which inherits these functions from the `SerialManager`).
