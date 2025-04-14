package me.zavdav.zcore.internal.util

import java.util.ResourceBundle

private val bundle = ResourceBundle.getBundle("messages")

internal fun getMessage(key: String): String = bundle.getString(key)

internal fun String.colored() = replace("&([0-9a-f])".toRegex(), "§$1")

internal fun tl(key: String, vararg args: Any): String {
    var message = getMessage(key).colored()
    for (i in args.indices) {
        message = message.replace("{$i}", args[i].toString())
    }
    return message
}

internal fun fmt(message: String, vararg pairs: Pair<String, Any>): String {
    var message = message.colored()
    for (pair in pairs) {
        message = message.replace("{${pair.first.uppercase()}}", pair.second.toString())
    }
    return message
}