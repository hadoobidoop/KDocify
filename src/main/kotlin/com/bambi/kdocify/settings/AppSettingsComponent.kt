package com.bambi.kdocify.settings

import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel
import com.intellij.ui.components.JBTextField

class AppSettingsComponent {
    val panel: JPanel
    private val serviceNameTextField = JBTextField()

    init {
        panel = FormBuilder.createFormBuilder()
            .addLabeledComponent("Set service name", serviceNameTextField)
            .addTooltip(Strings.settingsServiceNameTooltip)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    val preferredFocusedComponent: JComponent
        get() = serviceNameTextField

    var serviceName: String
        get() = serviceNameTextField.text
        set(newText) {
            serviceNameTextField.text = newText
        }
}
