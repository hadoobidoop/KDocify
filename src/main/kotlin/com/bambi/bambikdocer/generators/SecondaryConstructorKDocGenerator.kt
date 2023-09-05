package com.bambi.bambikdoc.generators

import org.jetbrains.kotlin.psi.KtSecondaryConstructor

/**
 * Secondary constructor Kotlin documentation generator.
 *
 * @property ktConstructor [KtSecondaryConstructor]
 * @constructor Create [SecondaryConstructorKDocGenerator]
 */
class SecondaryConstructorKDocGenerator(private val ktConstructor: KtSecondaryConstructor): KDocGenerator {
    override fun getGeneratedComment(): String {
        with(ktConstructor) {
            val builder = StringBuilder()
            builder.appendLine("/**")
                    .appendLine("* Secondary constructor for [${name}].")
                    .appendLine("*")

            if (typeParameters.isNotEmpty())
                builder.appendLine(typeParameters.toKdocParams())

            if (valueParameters.isNotEmpty())
                builder.appendLine(valueParameters.toKdocParams())

            builder.appendLine("*/")
            return builder.toString()
        }
    }
}