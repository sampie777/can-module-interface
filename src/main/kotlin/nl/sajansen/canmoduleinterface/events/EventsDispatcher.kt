package nl.sajansen.canmoduleinterface.events

import nl.sajansen.canmoduleinterface.serial.SerialConnectionState
import org.slf4j.LoggerFactory
import java.awt.Component
import javax.swing.JFrame

object EventsDispatcher : GuiEventListener, SerialEventListener {
    private val logger = LoggerFactory.getLogger(EventsDispatcher::class.java.name)

    var currentFrame: JFrame? = null

    private val guiEventListeners: HashSet<GuiEventListener> = HashSet()
    private val serialEventListeners: HashSet<SerialEventListener> = HashSet()

    /*
        GuiEventListener events
     */
    override fun refreshNotifications() {
        guiEventListeners.toTypedArray().forEach {
            it.refreshNotifications()
        }
    }

    override fun onConfigSettingsSaved() {
        guiEventListeners.toTypedArray().forEach {
            it.onConfigSettingsSaved()
        }
    }

    override fun windowClosing(window: Component?) {
        guiEventListeners.toTypedArray().forEach {
            it.windowClosing(window)
        }
    }

    /*
        SerialEventListener
     */
    override fun onSerialConnectionChanged(value: SerialConnectionState) {
        serialEventListeners.toTypedArray().forEach {
            it.onSerialConnectionChanged(value)
        }
    }

    override fun onSerialDataReceived(data: List<String>) {
        serialEventListeners.toTypedArray().forEach {
            it.onSerialDataReceived(data)
        }
    }

    override fun onSerialDataSend(data: String) {
        serialEventListeners.toTypedArray().forEach {
            it.onSerialDataSend(data)
        }
    }

    fun register(component: Any) {
        if (component is GuiEventListener) {
            logger.info("Registering GuiEventListener: ${component::class.java}")
            guiEventListeners.add(component)
        }
        if (component is SerialEventListener) {
            logger.info("Registering SerialEventListener: ${component::class.java}")
            serialEventListeners.add(component)
        }
    }

    fun unregister(component: GuiEventListener) {
        logger.info("Unregistering GuiEventListener: ${component::class.java}")
        guiEventListeners.remove(component)
    }

    fun unregister(component: SerialEventListener) {
        logger.info("Unregistering SerialEventListener: ${component::class.java}")
        serialEventListeners.remove(component)
    }

    fun registeredComponents() = guiEventListeners
}