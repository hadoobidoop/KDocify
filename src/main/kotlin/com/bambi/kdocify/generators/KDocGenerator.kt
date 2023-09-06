package com.bambi.kdocify.generators

import com.intellij.psi.*

const val NEXT_LINE = "\n"

interface KDocGenerator {
    fun getGeneratedComment(): String

    fun List<PsiNameIdentifierOwner>.toKdocParams(keyword: String = "@param"): String =
        joinToString(separator = NEXT_LINE/*, transform = { "* $it" }*/) { "* $keyword ${it.name}" }
    fun StringBuilder.appendLine(line: String): StringBuilder = append(line).append(NEXT_LINE)
}


