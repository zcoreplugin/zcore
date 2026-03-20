package me.zavdav.zcore.util

import java.util.regex.Pattern

internal fun String.wildcardMatchesIgnoreCase(regex: String): Boolean {
    if (regex.isEmpty()) {
        return this.isEmpty()
    }
    val sanitizedRegex = "^" + regex
        .replace("[\\\\\\[(){^$|?+.]".toRegex(), "\\\\$0")
        .replace(Pattern.quote("*").toRegex(), ".+") + "$"
    return Pattern.compile(sanitizedRegex, Pattern.CASE_INSENSITIVE)
        .matcher(this)
        .find()
}