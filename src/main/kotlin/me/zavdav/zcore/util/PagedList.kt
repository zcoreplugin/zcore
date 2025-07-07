package me.zavdav.zcore.util

import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.TextWrapper.CHAT_WINDOW_WIDTH
import org.bukkit.craftbukkit.TextWrapper.widthInPixels

class PagedList(
    elements: List<String>,
    rows: Int,
    columns: Int
) {

    private val pages = mutableListOf<Page>()

    init {
        require(rows > 0 && columns > 0) { "Invalid dimensions: $rows x $columns" }

        if (!elements.isEmpty()) {
            var page = Page(rows, columns)
            pages.add(page)
            for (element in elements) {
                if (page.add(element)) continue
                page = Page(rows, columns)
                pages.add(page)
            }
        }
    }

    fun pages(): Int = pages.size

    fun print(page: Int, target: CommandSender, color: ChatColor = ChatColor.WHITE) {
        pages[page].print(target, color)
    }

    private class Page(val rows: Int, val columns: Int) {

        private val lines = mutableListOf<String>()

        fun add(string: String): Boolean {
            val element = "$string "
            val lastLine = lines.lastOrNull() ?: ""
            val elementWidth = widthInPixels(element)
            val lineWidth = widthInPixels(lastLine)

            if (lineWidth + elementWidth < CHAT_WINDOW_WIDTH && !lines.isEmpty()) {
                val line = padString(lastLine + element)
                lines[lines.lastIndex] = line
                return true
            }

            if (lines.size >= rows) return false
            lines.add(padString(string))
            return true
        }

        private fun padString(string: String): String {
            val width = widthInPixels(string)
            val columnWidth = CHAT_WINDOW_WIDTH / columns
            val spaceWidth = widthInPixels(" ")
            val spanningCols = width / columnWidth + 1
            val padding = spanningCols * columnWidth - width

            return string + " ".repeat(padding / spaceWidth)
        }

        fun print(target: CommandSender, color: ChatColor) {
            lines.forEach { target.sendMessage("$color$it") }
        }

    }

}