package nl.sajansen.canmoduleinterface.gui

import java.awt.Component

interface Refreshable {
    fun refreshNotifications() {}
    fun onConfigSettingsSaved() {}

    fun windowClosing(window: Component?) {}
}