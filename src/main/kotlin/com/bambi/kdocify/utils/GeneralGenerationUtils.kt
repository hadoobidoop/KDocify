package com.bambi.kdocify.utils

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

