package nl.sajansen.canmoduleinterface.gui.updater

import nl.sajansen.canmoduleinterface.ApplicationInfo
import nl.sajansen.canmoduleinterface.gui.Theme
import nl.sajansen.canmoduleinterface.utils.gui.ClickableLinkComponent
import org.slf4j.LoggerFactory
import java.awt.Dimension
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.border.EmptyBorder

class UpdatePopupContent(val version: String) : JPanel() {
    private val logger = LoggerFactory.getLogger(UpdatePopupContent::class.java.name)

    init {
        initGui()
    }

    private fun initGui() {
        layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
        border = EmptyBorder(0, 20, 10, 20)

        val infoLabel = JLabel(
            """
            <html>
            <h3>${ApplicationInfo.name} $version is available</h3>
            <p>A new version is available. You currently have ${ApplicationInfo.version}.</p>
            <p>Click 'Download' below to open the download webpage to get the new version.</p>
            </html>
        """.trimIndent()
        )
        infoLabel.font = Theme.mediumFont

        val releaseNotesLink = ClickableLinkComponent("Release notes", ApplicationInfo.updatesInfoUrl)
        releaseNotesLink.font = Theme.mediumFont

        add(infoLabel)
        add(Box.createRigidArea(Dimension(0, 40)))
        add(releaseNotesLink)
    }
}
