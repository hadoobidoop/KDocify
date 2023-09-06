package com.bambi.kdocify.settings

import Strings
import com.intellij.openapi.options.Configurable
import org.jetbrains.annotations.Nls
import javax.swing.JComponent
import javax.swing.JPanel

class AppSettingsConfigurable : Configurable {
    private var appSettingsComponent: AppSettingsComponent? = null

    @Nls(capitalization = Nls.Capitalization.Title)
    override fun getDisplayName(): String = Strings.settingsTitle

    override fun getPreferredFocusedComponent(): JComponent =
        appSettingsComponent?.preferredFocusedComponent ?: JPanel()

    override fun createComponent(): JComponent {
        appSettingsComponent = AppSettingsComponent()
        return appSettingsComponent!!.panel
    }

    override fun isModified(): Boolean {
        return appSettingsComponent?.serviceName != AppSettingsState.status.serviceName
    }

    override fun apply() {
        appSettingsComponent?.let {
            AppSettingsState.status.serviceName = it.serviceName
        }
    }

    override fun reset() {
        appSettingsComponent?.serviceName = AppSettingsState.status.serviceName
    }

    override fun disposeUIResources() {
        appSettingsComponent = null
    }
}

