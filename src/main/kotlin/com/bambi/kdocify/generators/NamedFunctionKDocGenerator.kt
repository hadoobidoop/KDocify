package com.bambi.kdocify.generators

import com.bambi.kdocify.domain.Kdoc
import com.bambi.kdocify.domain.Tag
import com.bambi.kdocify.domain.getDefaultCommentName
import com.bambi.kdocify.domain.kdoc
import com.bambi.kdocify.settings.AppSettingsState
import com.bambi.kdocify.utils.getCheckedType
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * [안녕] Named Function K Doc Generator.
 *
 */
class NamedFunctionKDocGenerator(private val function: KtNamedFunction) : KDocGenerator {
    override fun getGeneratedComment(): Kdoc {
        return with(function) {
            kdoc {
                title {
                    getDefaultCommentName(
                        serviceName = AppSettingsState.status.serviceName,
                        title = name,
                        withDot = true
                    )
                }

                tags {
                    if (receiverTypeReference != null) {
                        receiver(name = receiverTypeReference?.text.getCheckedType())
                    }

                    if (typeParameters.isNotEmpty()) {
                        addAll(typeParameters.map { Tag.Parameter(it.name) })
                    }

                    if (valueParameters.isNotEmpty()) {
                        addAll(valueParameters.map { Tag.Parameter(it.name) })
                    }

                    if (isTypedReferenceFunction()) {
                        `return`(typeReference?.text.getCheckedType())
                    } else if (function.text.isFunctionWithHiddenTypedReference()) {
                        `return`("")
                    }
                }
            }
        }
    }

    private fun KtNamedFunction.isTypedReferenceFunction(): Boolean =
        typeReference != null && typeReference?.text != "Unit"


    private fun String?.isFunctionWithHiddenTypedReference(): Boolean {
        var bakeCounter = 0
        var endOfSignatureFound = false
        this?.forEach { char ->
            when {
                endOfSignatureFound -> {
                    if (char == '=')
                        return true
                    else if (char == '{')
                        return false
                }

                char == '(' -> {
                    bakeCounter++
                }

                char == ')' -> {
                    endOfSignatureFound = --bakeCounter == 0
                }
            }
        }
        return false
    }
}
