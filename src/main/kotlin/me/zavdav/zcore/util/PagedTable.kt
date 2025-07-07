package me.zavdav.zcore.util

import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.TextWrapper.CHAT_WINDOW_WIDTH
import org.bukkit.craftbukkit.TextWrapper.widthInPixels

class PagedTable<T>(
    elements: List<T>,
    rows: Int = elements.size,
    transform: (Int, T) -> Array<Pair<String, Int>>
) {

    private val pages = mutableListOf<Page>()

    init {
        require(rows > 0) { "Invalid row count: $rows" }

        if (!elements.isEmpty()) {
            var page = Page(rows)
            pages.add(page)
            for (i in elements.indices) {
                val row = transform(i, elements[i])
                if (page.add(row)) continue
                page = Page(rows)
                pages.add(page)
            }
        }
    }

    fun pages(): Int = pages.size

    fun print(page: Int, target: CommandSender) {
        pages[page].print(target)
    }

    private class Page(val rows: Int) {

        private val lines = mutableListOf<String>()

        fun add(row: Array<Pair<String, Int>>): Boolean {
            if (lines.size == rows) return false

            val singleCellWidth = CHAT_WINDOW_WIDTH / row.sumOf { it.second }
            val spaceWidth = widthInPixels(" ")
            val sb = StringBuilder()
            var currentWidth = 0

            for (i in row.indices) {
                val content = "${row[i].first.trimEnd()} "
                val widthToReach = row.toList().subList(0, i + 1).sumOf { it.second } * singleCellWidth

                sb.append(content)
                currentWidth += widthInPixels(content)

                while (currentWidth + spaceWidth < widthToReach) {
                    sb.append(" ")
                    currentWidth += spaceWidth
                }
            }

            lines.add(sb.toString())
            return true
        }

        fun print(target: CommandSender) {
            lines.forEach { target.sendMessage(it) }
        }

    }

}