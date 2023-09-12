package com.bambi.kdocify.generators

import com.bambi.kdocify.domain.Kdoc
import com.bambi.kdocify.domain.Tag
import com.bambi.kdocify.domain.getDefaultCommentName
import com.bambi.kdocify.domain.kdoc
import com.bambi.kdocify.settings.AppSettingsState
import com.bambi.kdocify.utils.getEndOfLineComment
import org.jetbrains.kotlin.psi.KtClass

/**
 * Class K Doc Generator.
 *
 * @property klass dksdf
 * @constructor Create [ClassKDocGenerator]
 */
class ClassKDocGenerator(
    private val klass: KtClass,//dksdf
) : KDocGenerator {
    override fun getGeneratedComment(): Kdoc {

        return with(klass) {
            val (properties, parameters) = primaryConstructor?.valueParameters?.partition {
                it.hasValOrVar()
            } ?: Pair(emptyList(), emptyList())

            kdoc {
                title {
                    getDefaultCommentName(
                        serviceName = AppSettingsState.status.serviceName,
                        title = klass.name,
                        withDot = true
                    )
                }
                tags {
                    if (typeParameters.isNotEmpty()) {
                        addAll(typeParameters.map { Tag.Parameter(it.name) })
                    }
                    if (parameters.isNotEmpty()) {
                        addAll(parameters.map { Tag.Parameter(it.name) })
                        addAll(properties.map { Tag.Property(name = it.name, it.getEndOfLineComment() ?: "") })
                    }

                    if (properties.isNotEmpty()) {
                        addAll(properties.map {
                            Tag.Property(
                                name = it.name,
                                description = (it.getEndOfLineComment() ?: "")
                            )
                        })
                    }
                    if (properties.isNotEmpty() || parameters.isNotEmpty()) {
                        constructor("Create [$name]")
                    } else {
                        constructor(
                            name = "Create empty constructor for ${
                                getDefaultCommentName(
                                    serviceName = AppSettingsState.status.serviceName,
                                    title = klass.name,
                                    withDot = false
                                )
                            }"
                        )
                    }
                }
            }
        }
    }
}
