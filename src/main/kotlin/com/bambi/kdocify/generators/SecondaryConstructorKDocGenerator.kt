package com.bambi.kdocify.generators

import org.jetbrains.kotlin.psi.KtSecondaryConstructor

class SecondaryConstructorKDocGenerator(private val ktConstructor: KtSecondaryConstructor) : KDocGenerator {
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