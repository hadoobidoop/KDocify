package com.bambi.kdocify.generators

import com.intellij.psi.*

const val NEXT_LINE = "\n"

interface KDocGenerator {
    fun getGeneratedComment(): String

    /**
     * Convert list of psi params to generated comment.
     *
     * @receiver List of [PsiNameIdentifierOwner]
     * @param keyword Keyword
     * @return Comment
     */
    fun List<PsiNameIdentifierOwner>.toKdocParams(keyword: String = "@param"): String =
        joinToString(separator = NEXT_LINE/*, transform = { "* $it" }*/) { "* $keyword ${it.name}" }

    /**
     * Append line.
     *
     * @receiver [StringBuilder]
     * @param line Line
     * @return [StringBuilder]
     */
    fun StringBuilder.appendLine(line: String): StringBuilder = append(line).append(NEXT_LINE)
}


