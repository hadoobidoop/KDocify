package com.bambi.kdocify.generators

import com.bambi.kdocify.domain.Kdoc
import com.bambi.kdocify.domain.Tag
import com.bambi.kdocify.domain.kdoc
import org.jetbrains.kotlin.psi.KtSecondaryConstructor
class SecondaryConstructorKDocGenerator(private val ktConstructor: KtSecondaryConstructor) : KDocGenerator {
    override fun getGeneratedComment(): Kdoc {
        return with(ktConstructor) {
            kdoc {
                title { "Secondary constructor for [${name}]." }
                tags {
                    if (typeParameters.isNotEmpty()) {
                        addAll(typeParameters.map { Tag.Parameter(it.name) })
                    }
                    if (valueParameters.isNotEmpty()) {
                        addAll(valueParameters.map { Tag.Parameter(it.name) })
                    }
                }
            }
        }

    }
}