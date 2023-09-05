package com.bambi.kdocify.settings

import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel
import com.intellij.ui.components.JBTextField

class AppSettingsComponent {
    val panel: JPanel
    private val includePreReleasesTextField = JBTextField()

    init {
        panel = FormBuilder.createFormBuilder()
            .addLabeledComponent("Set service name", includePreReleasesTextField)
            .addTooltip(Strings.settingsIncludePreReleasesTooltip)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    val preferredFocusedComponent: JComponent
        get() = includePreReleasesTextField

    var includePreReleases: String
        get() = includePreReleasesTextField.text
        set(newText) {
            includePreReleasesTextField.text = newText
        }
}
