package me.zavdav.zcore.util

import me.zavdav.zcore.config.ZCoreConfig
import org.bukkit.ChatColor
import org.bukkit.craftbukkit.TextWrapper.CHAT_WINDOW_WIDTH
import org.bukkit.craftbukkit.TextWrapper.widthInPixels
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

fun alignText(vararg pairs: Pair<Any, Int>): String {
    val singleCellWidth = CHAT_WINDOW_WIDTH / pairs.sumOf { it.second }
    val spaceWidth = widthInPixels(" ")
    val sb = StringBuilder()
    var currentWidth = 0

    for (i in pairs.indices) {
        val content = "${pairs[i].first.toString().trimEnd()} "
        val widthToReach = pairs.toList().subList(0, i + 1).sumOf { it.second } * singleCellWidth

        sb.append(content)
        currentWidth += widthInPixels(content)
        while (currentWidth + spaceWidth <= widthToReach) {
            sb.append(" ")
            currentWidth += spaceWidth
        }
    }

    return sb.toString().trimEnd()
}