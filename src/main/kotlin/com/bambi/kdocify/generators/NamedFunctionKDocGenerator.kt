package com.bambi.kdocify.generators

import com.bambi.kdocify.utils.getCheckedType
import com.bambi.kdocify.utils.getDefaultCommentFromName
import com.intellij.psi.PsiNameIdentifierOwner
import org.jetbrains.kotlin.psi.KtNamedFunction

class NamedFunctionKDocGenerator(private val function: KtNamedFunction) : KDocGenerator {

    override fun getGeneratedComment(): String = buildString {
        appendLine("/**")
        appendFunctionName()
        appendControllerMethodIfNeeded()
        appendEmptyFunctionSpaceIfNeeded()
        appendReceiverReference()
        appendTypeParameters()
        appendValueParameters()
        appendReturnAnnotation()
        appendLine("*/")
    }

    private fun StringBuilder.appendFunctionName() {
        appendLine("* ${function.name.getDefaultCommentFromName()}")
    }

    private fun StringBuilder.appendControllerMethodIfNeeded() {
        if (isControllerMethod(function)) {
            appendLine("*")
            appendLine("* called by: ")
        }
    }

    private fun StringBuilder.appendEmptyFunctionSpaceIfNeeded() {
        if (function.isNotEmptyFunction()) appendLine("*")
    }

    private fun StringBuilder.appendReceiverReference() {
        function.receiverTypeReference?.let {
            appendLine("* @receiver ${it.text.getCheckedType()}")
        }
    }

    private fun StringBuilder.appendTypeParameters() {
        if (function.typeParameters.isNotEmpty()) {
            appendLine(function.typeParameters.toKdocParams())
        }
    }

    private fun StringBuilder.appendValueParameters() {
        if (function.valueParameters.isNotEmpty()) {
            appendLine(function.valueParameters.toKdocParams())
        }
    }

    private fun StringBuilder.appendReturnAnnotation() {
        when {
            function.isTypedReferenceFunction() ->
                appendLine("* @return ${function.typeReference?.text.getCheckedType()}")

            function.text.isFunctionWithHiddenTypedReference() ->
                appendLine("* @return")
        }
    }

    private fun isControllerMethod(ktNamedFunction: KtNamedFunction): Boolean {
        val mappings = listOf("GetMapping", "PostMapping", "PutMapping", "DeleteMapping", "PatchMapping")
        return ktNamedFunction.annotationEntries.any { it.shortName?.asString() in mappings }
    }

    private fun KtNamedFunction.isNotEmptyFunction(): Boolean =
        receiverTypeReference != null ||
                typeParameters.isNotEmpty() ||
                valueParameters.isNotEmpty() ||
                isTypedReferenceFunction() ||
                text.isFunctionWithHiddenTypedReference()

    private fun KtNamedFunction.isTypedReferenceFunction(): Boolean =
        typeReference?.text?.let { it != "Unit" } ?: false

    override fun List<PsiNameIdentifierOwner>.toKdocParams(keyword: String): String =
        joinToString(separator = "\n") {
            "* $keyword ${it.name} ${it.name.getDefaultCommentFromName(withDot = false)}"
        }

    private fun String?.isFunctionWithHiddenTypedReference(): Boolean {
        var braceCounter = 0
        var endOfSignatureFound = false
        this?.forEach { char ->
            when {
                endOfSignatureFound -> {
                    if (char == '=') return true
                    else if (char == '{') return false
                }

                char == '(' -> braceCounter++
                char == ')' -> endOfSignatureFound = --braceCounter == 0
            }
        }
        return false
    }
}
