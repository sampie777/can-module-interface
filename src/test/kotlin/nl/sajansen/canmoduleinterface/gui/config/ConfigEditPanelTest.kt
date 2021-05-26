package nl.sajansen.canmoduleinterface.gui.config

import nl.sajansen.canmoduleinterface.gui.notifications.Notifications
import kotlin.test.Test
import kotlin.test.assertEquals

class ConfigEditPanelTest {
    @Test
    fun `test config edit panel is loading without problems`() {
        Notifications.clear()

        ConfigEditPanel()

        assertEquals(0, Notifications.unreadNotifications)
    }
}