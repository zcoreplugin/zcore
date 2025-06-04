package me.zavdav.zcore.util

import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.TextWrapper.CHAT_WINDOW_WIDTH
import org.bukkit.craftbukkit.TextWrapper.widthInPixels

internal class GridPage(rows: Int, columns: Int) {

    private val rows: Int = rows.coerceAtLeast(1)
    private val columnWidth: Int = CHAT_WINDOW_WIDTH / columns.coerceAtLeast(1)
    private val whitespaceWidth: Int = widthInPixels(" ")

    val lines: MutableList<String> = mutableListOf("")
    var header: String? = null
        set(value) {
            if (!value.isNullOrEmpty()) {
                var header = "&7=> &f$value &7<=".colored()
                while (widthInPixels("$header=") < CHAT_WINDOW_WIDTH)
                    header += "="
                field = header
            }
        }

    fun add(string: String): Boolean {
        val element = "$string "
        val lastLine = lines.last()
        val elementWidth = widthInPixels(element)
        val lineWidth = widthInPixels(lastLine)

        if (lineWidth + elementWidth < CHAT_WINDOW_WIDTH) {
            val line = padString(lastLine + element)
            lines[lines.lastIndex] = line
            return true
        }

        if (lines.size >= rows) return false
        lines.add(padString(string))
        return true
    }

    fun print(target: CommandSender) {
        header?.let { target.sendMessage(it) }
        lines.forEach { target.sendMessage(it) }
    }

    private fun padString(string: String): String {
        val width = widthInPixels(string)
        val spanningCols = width / columnWidth + 1
        val padding = spanningCols * columnWidth - width
        return string + " ".repeat(padding / whitespaceWidth)
    }

}