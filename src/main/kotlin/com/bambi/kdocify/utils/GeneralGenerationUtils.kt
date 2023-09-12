package com.bambi.kdocify.utils

fun String?.getCheckedType(): String {
    // Check if the string is null or empty, return an empty string in that case
    if (this.isNullOrEmpty()) return ""

    // Find the first exclusion that matches the string
    val matchingExclusion = exclusions.find { this.contains(it) }

    return matchingExclusion?.let {
        // If a matching exclusion is found, return it in square brackets
        "[$it]"
    } ?: run {
        // If no matching exclusion is found, determine the type based on the string
        val baseType = this.split("<").first()
        val isNullable = this.lastOrNull() == '?'

        if (isNullable) {
            // If it's nullable, return the base type followed by "or null" in square brackets
            "[$baseType or null]"
        } else {
            // If it's not nullable, return the base type in square brackets
            "[$baseType]"
        }
    }
}
