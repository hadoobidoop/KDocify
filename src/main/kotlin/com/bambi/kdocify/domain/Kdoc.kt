package com.bambi.kdocify.domain

import org.apache.commons.lang3.StringUtils
import java.util.*

data class Kdoc(
    private val title: String? = null,
    private val description: String? = null,
    private val tags: List<Tag> = listOf()
) {
    override fun toString() =
        buildString {
            appendLine("/**")
            appendLine(" * $title")
            description?.let { appendLine(" * $it") }
            appendLine(" *").takeIf { tags.isNotEmpty() }
            tags.asSequence().forEach { appendLine(it.asKdocString()) }
            append(" */")
        }
}

/**
 * Kdoc.
 *
 * @param init
 * @return [Kdoc]
 */
fun kdoc(init: KdocBuilder.() -> Unit): Kdoc {
    return KdocBuilder().apply(init).build()
}

class KdocBuilder {
    var title: String? = null
    var description: String? = null
    private val _tags = mutableListOf<Tag>()

    fun title(lambda: () -> String) {
        title = lambda()
    }

    fun description(lambda: () -> String) {
        description = lambda()
    }

    fun tags(lambda: TagListBuilder.() -> Unit) {
        _tags.addAll(TagListBuilder().apply(lambda).build())
    }

    fun build(): Kdoc {
        return Kdoc(title, description, _tags)
    }
}

//TODO serviceName도 null 가능, null 일때는 []가 안보여야 함
fun getDefaultCommentName(serviceName: String, title: String? = null, withDot: Boolean) =
    buildString {
        title?.let {
            if (serviceName.isNotBlank()) append("[$serviceName] ${formatCamelCaseToTitleCase(it)}")
            else append(formatCamelCaseToTitleCase(it))
        } ?: append("TODO")
        if (withDot) append(".")
    }

private fun formatCamelCaseToTitleCase(title: String): String {
    return StringUtils.splitByCharacterTypeCamelCase(title).joinToString(separator = " ") { word ->
        word.trim().lowercase().replaceFirstChar { it.titlecase(Locale.getDefault()) }
    }
}
