package com.bambi.bambikdoc.generators

import com.bambi.bambikdoc.utils.getDefaultCommentFromName
import org.jetbrains.kotlin.psi.KtClass


/**
 * Interface Kotlin documentation generator.
 *
 * @property klass [KtClass]
 * @constructor Create [InterfaceKDocGenerator]
 */
class InterfaceKDocGenerator(private val klass: KtClass) : KDocGenerator {
    override fun getGeneratedComment(): String = StringBuilder()
        .appendLine("/**")
            .appendLine("* ${klass.name.getDefaultCommentFromName()}")
            .appendLine("*/")
            .toString()
}