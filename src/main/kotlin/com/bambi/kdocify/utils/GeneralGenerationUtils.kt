package com.bambi.kdocify.utils

import com.bambi.kdocify.settings.AppSettingsState
import org.apache.commons.lang.StringUtils
import java.util.*

/**
 * Get default comment for function or class name.
 *
 * @param isCapitalized Is comment should be capitalized
 * @receiver Class or function name
 * @return Generated comment
 */
fun String?.getDefaultCommentFromName(isCapitalized: Boolean = true, withDot: Boolean = true): String {
    this ?: return "TODO"

    var comment = ""
    this.split("_").filter { it.isNotEmpty() }.forEach { bitOfFunctionName ->
        StringUtils.splitByCharacterTypeCamelCase(bitOfFunctionName).forEach { word ->
            comment += " ${word.trim().lowercase()}"
        }
    }

    comment = if (isCapitalized) {
        comment.trim().replaceFirstChar {
            if (it.isLowerCase())
                it.titlecase(Locale.getDefault())
            else
                it.toString()
        }
    } else {
        comment.trim()
    }
    val includePreReleaseValue = AppSettingsState.instance.includePreRelease

    return if (withDot)
        "[$includePreReleaseValue] $comment."
    else
        comment
}

/**
 * Check type.
 *
 * @receiver Type
 * @return Generated comment
 */
fun String?.getCheckedType(): String {
    this ?: return ""

    exclusions.forEach {
        if (this.contains(it))
            return ""
    }

    val nameWithoutGenericType = this.split("<").first()
    return if (this.lastOrNull() == '?')
        "[${nameWithoutGenericType.removeSuffix("?")}] or null"
    else
        "[$nameWithoutGenericType]"
}

