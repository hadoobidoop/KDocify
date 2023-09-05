package com.bambi.kdocify.generators

import com.bambi.kdocify.utils.getDefaultCommentFromName
import org.jetbrains.kotlin.psi.KtClass

class InterfaceKDocGenerator(private val klass: KtClass) : KDocGenerator {
    override fun getGeneratedComment(): String = StringBuilder()
        .appendLine("/**")
            .appendLine("* ${klass.name.getDefaultCommentFromName()}")
            .appendLine("*/")
            .toString()
}