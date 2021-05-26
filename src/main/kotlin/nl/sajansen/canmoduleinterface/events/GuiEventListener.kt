package nl.sajansen.canmoduleinterface.events

import java.awt.Component

interface GuiEventListener {
    fun refreshNotifications() {}
    fun onConfigSettingsSaved() {}
    fun windowClosing(window: Component?) {}
}