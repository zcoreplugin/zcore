package me.zavdav.zcore.util

import me.zavdav.zcore.config.ZCoreConfig
import org.bukkit.ChatColor
import java.util.ResourceBundle

fun String.colored() = replace("&([0-9a-f])".toRegex(), "§$1")

fun line(color: ChatColor): String =
    "$color-----------------------------------------------------"

fun local(key: String, vararg replacements: Any): String {
    var message = ResourceBundle.getBundle("lang").getString(key)
        .replaceFirst("<prefix>", ZCoreConfig.getString("general.command-prefix") + "&f")
        .colored()

    for (i in replacements.indices) {
        message = message.replace("{$i}", replacements[i].toString())
    }

    return message
}

fun formatted(message: String, vararg replacements: Pair<String, Any>): String {
    var message = message.colored()
    for (repl in replacements) {
        message = message.replace("{${repl.first.uppercase()}}", repl.second.toString())
    }

    return message
}