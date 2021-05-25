package nl.sajansen.canmoduleinterface.gui.menu

import javax.swing.JMenuBar

class MenuBar : JMenuBar() {
    init {
        add(ApplicationMenu())
    }
}