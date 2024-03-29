package nl.sajansen.canmoduleinterface

import nl.sajansen.canmoduleinterface.config.Config
import nl.sajansen.canmoduleinterface.events.EventsDispatcher
import nl.sajansen.canmoduleinterface.gui.mainFrame.MainFrame
import nl.sajansen.canmoduleinterface.gui.notifications.Notifications
import nl.sajansen.canmoduleinterface.hardware.CAN
import nl.sajansen.canmoduleinterface.updater.UpdateChecker
import nl.sajansen.canmoduleinterface.utils.getCurrentJarDirectory
import org.slf4j.LoggerFactory
import java.awt.EventQueue
import kotlin.system.exitProcess

private val logger = LoggerFactory.getLogger("Application")

fun main(args: Array<String>) {
    ApplicationRuntimeSettings.testing = false

    logger.info("Starting application ${ApplicationInfo.artifactId}:${ApplicationInfo.version}")
    logger.info("Executing JAR directory: " + getCurrentJarDirectory(ApplicationInfo).absolutePath)

    Notifications.enablePopups = !ApplicationRuntimeSettings.testing

    Config.enableWriteToFile(!ApplicationRuntimeSettings.virtualConfig && !ApplicationRuntimeSettings.testing)
    Config.load()
    Config.save()

    EventQueue.invokeLater {
        MainFrame.createAndShow()
    }

    if ("--clear-update-history" in args) {
        UpdateChecker().clearUpdateHistory()
    }
    UpdateChecker().checkForUpdates()
}

fun exitApplication() {
    logger.info("Shutting down application")

    CAN.disconnect()

    MainFrame.getInstance()?.saveWindowPosition()

    try {
        logger.info("Closing windows...")
        EventsDispatcher.windowClosing(MainFrame.getInstance())
    } catch (t: Throwable) {
        logger.warn("Failed to correctly close windows")
        t.printStackTrace()
    }

    logger.info("Shutdown finished")
    exitProcess(0)
}
