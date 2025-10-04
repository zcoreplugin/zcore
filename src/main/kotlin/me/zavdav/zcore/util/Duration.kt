package me.zavdav.zcore.util

import java.util.regex.Pattern

private const val SECONDS_PER_YEAR = 31_536_000
private const val SECONDS_PER_MONTH = 2_592_000
private const val SECONDS_PER_WEEK = 604_800
private const val SECONDS_PER_DAY = 86400
private const val SECONDS_PER_HOUR = 3600
private const val SECONDS_PER_MINUTE = 60

internal val DURATION_PATTERN = Pattern.compile(
    "(?:(\\d+)\\s?y\\s?)?" +
    "(?:(\\d+)\\s?mo\\s?)?" +
    "(?:(\\d+)\\s?w\\s?)?" +
    "(?:(\\d+)\\s?d\\s?)?" +
    "(?:(\\d+)\\s?h\\s?)?" +
    "(?:(\\d+)\\s?m\\s?)?" +
    "(?:(\\d+)\\s?s)?",
    Pattern.CASE_INSENSITIVE
)

internal fun formatDuration(millis: Long): String {
    var duration = millis / 1000

    val years = duration / SECONDS_PER_YEAR
    duration -= years * SECONDS_PER_YEAR
    val days = duration / SECONDS_PER_DAY
    duration -= days * SECONDS_PER_DAY
    val hours = duration / SECONDS_PER_HOUR
    duration -= hours * SECONDS_PER_HOUR
    val minutes = duration / SECONDS_PER_MINUTE
    duration -= minutes * SECONDS_PER_MINUTE
    val seconds = duration

    val units = arrayOf(
        years to "y",
        days to "d",
        hours to "h",
        minutes to "m",
        seconds to "s"
    )

    val sb = StringBuilder()
    for (unit in units) {
        if (unit.first > 0) sb.append("${unit.first}${unit.second} ")
    }

    return if (sb.isEmpty()) "0s" else sb.substring(0, sb.length - 1)
}

internal fun parseDuration(duration: String): Long? {
    val matcher = DURATION_PATTERN.matcher(duration)
    if (!matcher.matches()) return null

    val years = matcher.group(1)?.toLongOrNull() ?: 0
    val months = matcher.group(2)?.toLongOrNull() ?: 0
    val weeks = matcher.group(3)?.toLongOrNull() ?: 0
    val days = matcher.group(4)?.toLongOrNull() ?: 0
    val hours = matcher.group(5)?.toLongOrNull() ?: 0
    val minutes = matcher.group(6)?.toLongOrNull() ?: 0
    val seconds = matcher.group(7)?.toLongOrNull() ?: 0

    return (
        years * SECONDS_PER_YEAR +
        months * SECONDS_PER_MONTH +
        weeks * SECONDS_PER_WEEK +
        days * SECONDS_PER_DAY +
        hours * SECONDS_PER_HOUR +
        minutes * SECONDS_PER_MINUTE +
        seconds
    ) * 1000
}