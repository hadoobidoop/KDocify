package com.bambi.kdocify.generators

import com.bambi.kdocify.domain.Kdoc
import com.bambi.kdocify.domain.getDefaultCommentName
import com.bambi.kdocify.domain.kdoc
import com.bambi.kdocify.settings.AppSettingsState
import org.jetbrains.kotlin.psi.KtClass

class InterfaceKDocGenerator(private val klass: KtClass) : KDocGenerator {
    override fun getGeneratedComment(): Kdoc {
        return kdoc {
            title {
                getDefaultCommentName(
                    serviceName = AppSettingsState.status.serviceName,
                    title = klass.name,
                    withDot = true
                )
            }
        }
    }
}