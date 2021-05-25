package nl.sajansen.canmoduleinterface.gui.notifications

import java.util.*

data class Notification(val message: String, val subject: String = "") {
    val timestamp = Date()
}