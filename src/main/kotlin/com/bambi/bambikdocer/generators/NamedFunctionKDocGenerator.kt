package com.bambi.bambikdoc.generators

import com.bambi.bambikdoc.utils.getCheckedType
import com.bambi.bambikdoc.utils.getDefaultCommentFromName
import com.intellij.psi.PsiNameIdentifierOwner
import org.jetbrains.kotlin.psi.KtNamedFunction


/**
 * Named function Kotlin documentation generator.
 *
 * @property function [KtNamedFunction]
 * @constructor Create [NamedFunctionKDocGenerator]
 */
class NamedFunctionKDocGenerator(private val function: KtNamedFunction) : KDocGenerator {
    override fun getGeneratedComment(): String {
        with(function) {
            val builder = StringBuilder()
            builder.appendLine("/**")
                    .appendLine("* ${name.getDefaultCommentFromName()}")

            if (isNotEmptyFunction())
                builder.appendLine("*")

            if (receiverTypeReference != null)
                builder.appendLine("* @receiver ${receiverTypeReference?.text.getCheckedType()}")

            if (typeParameters.isNotEmpty())
                builder.appendLine(typeParameters.toKdocParams())

            if (valueParameters.isNotEmpty())
                builder.appendLine(valueParameters.toKdocParams())

            if (isTypedReferenceFunction())
                builder.appendLine("* @return ${typeReference?.text.getCheckedType()}")
            else if (function.text.isFunctionWithHiddenTypedReference())
                builder.appendLine("* @return")

            builder.appendLine("*/")
            return builder.toString()
        }
    }

    /**
     * Is not empty function.
     *
     * @receiver [KtNamedFunction]
     * @return Is not empty function
     */
    private fun KtNamedFunction.isNotEmptyFunction(): Boolean = receiverTypeReference != null ||
            typeParameters.isNotEmpty() || valueParameters.isNotEmpty() || isTypedReferenceFunction() ||
            text.isFunctionWithHiddenTypedReference()

    /**
     * Is typed reference function.
     *
     * @receiver [KtNamedFunction]
     * @return Is typed reference function
     */
    private fun KtNamedFunction.isTypedReferenceFunction(): Boolean =
        typeReference != null && typeReference?.text != "Unit"

    override fun List<PsiNameIdentifierOwner>.toKdocParams(keyword: String): String =
        joinToString(separator = NEXT_LINE) {
            "* $keyword ${it.name} ${it.name.getDefaultCommentFromName(withDot = false)}"
        }

    /**
     * Is function with hidden typed reference.
     *
     * @receiver [String] or null
     * @return Is function with hidden typed reference
     */
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
                char == '(' -> { bakeCounter++ }
                char ==  ')' -> { endOfSignatureFound = --bakeCounter == 0 }
            }
        }
        return false
    }
}