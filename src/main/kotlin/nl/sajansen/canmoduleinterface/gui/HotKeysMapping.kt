package nl.sajansen.canmoduleinterface.gui

import java.awt.event.KeyEvent

enum class HotKeysMapping(val key: Int) {
    QuitApplication(KeyEvent.VK_Q),
    Close(KeyEvent.VK_C),
    Save(KeyEvent.VK_S),

    ApplicationMenu(KeyEvent.VK_A),
    ShowConfig(KeyEvent.VK_S),
    ShowApplicationInfo(KeyEvent.VK_I),

    ChangePort(KeyEvent.VK_P),
    Connect(KeyEvent.VK_C),
}