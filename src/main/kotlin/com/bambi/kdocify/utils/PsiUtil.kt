package com.bambi.kdocify.utils

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.impl.source.tree.LeafPsiElement

fun PsiElement?.getEndOfLineComment(): String? {
    // Ensure the PsiElement is not null
    this?.let { psiElement ->
        var sibling: PsiElement? = psiElement.nextSibling

        // Traverse next siblings until we find a comment or a newline
        while (sibling != null) {
            when {
                sibling is LeafPsiElement && sibling.text.startsWith("//") -> return sibling.text.removePrefix("//").trim()
                sibling is PsiWhiteSpace && sibling.textContains('\n') -> return null
                else -> sibling = sibling.nextSibling
            }
        }
    }
    return null
}