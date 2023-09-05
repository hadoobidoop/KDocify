package com.bambi.bambikdoc.generators

import com.bambi.bambikdoc.utils.getDefaultCommentFromName
import org.jetbrains.kotlin.psi.KtClass


/**
 * Class Kotlin documentation generator.
 *
 * @property klass [KtClass]
 * @constructor Create [ClassKDocGenerator]
 */

/**
 *
 */
class ClassKDocGenerator(private val klass: KtClass) : KDocGenerator {
    override fun getGeneratedComment(): String {
        with(klass) {
            val builder = StringBuilder()
            builder.appendLine("/**")
                .appendLine("* ${name.getDefaultCommentFromName()}")
                .appendLine("*")

            if (typeParameters.isNotEmpty())
                builder.appendLine(typeParameters.toKdocParams())

            val (properties, parameters) = primaryConstructor?.valueParameters?.partition {
                it.hasValOrVar()
            } ?: Pair(emptyList(), emptyList())

            if (properties.isNotEmpty())
                builder.appendLine(properties.toKdocParams(keyword = "@property"))

            if (properties.isNotEmpty() || parameters.isNotEmpty())
                builder.appendLine("* @constructor Create [$name]")
            else
                builder.appendLine(
                    "* @constructor Create empty constructor for " +
                            name.getDefaultCommentFromName(isCapitalized = false, withDot = false)
                )

            if (parameters.isNotEmpty())
                builder.appendLine("*")
                    .appendLine(parameters.toKdocParams())

            builder.appendLine("*/")
            return builder.toString()
        }
    }
}