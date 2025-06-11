package me.zavdav.zcore.util

import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.TextWrapper.CHAT_WINDOW_WIDTH
import org.bukkit.craftbukkit.TextWrapper.widthInPixels

private val SPACE_WIDTH = widthInPixels(" ")
private val STROKE_WIDTH = widthInPixels("=")

internal class Page(
    private val header: String?,
    private val rows: List<String>
) {
    fun print(target: CommandSender) {
        header?.let { target.sendMessage(it) }
        rows.forEach { target.sendMessage(it) }
    }
}

internal class PageBuilder(builder: PageBuilder.() -> Unit) {

    private var header: String? = null
    private val rows = mutableListOf<String>()

    init { builder() }

    fun header(text: String) {
        val sb = StringBuilder("&7=> &f$text &7<=".colored())
        var currentWidth = widthInPixels(sb.toString())

        while (currentWidth + STROKE_WIDTH < CHAT_WINDOW_WIDTH) {
            sb.append("=")
            currentWidth += STROKE_WIDTH
        }

        header = sb.toString()
    }

    fun row(builder: RowBuilder.() -> Unit) {
        rows.add(RowBuilder().apply { builder() }.create())
    }

    fun list(columns: Int, list: List<String>) {
        rows.addAll(ListBuilder(columns, list).create())
    }

    fun create(): Page = Page(header, rows)

}

internal class RowBuilder {

    private val cells = mutableListOf<Cell>()

    fun cell(width: Int, content: Any) {
        cells.add(Cell(width, content.toString()))
    }

    fun create(): String {
        val singleCellWidth = CHAT_WINDOW_WIDTH / cells.sumOf { it.width }
        val sb = StringBuilder()
        var currentWidth = 0

        for (i in cells.indices) {
            val content = "${cells[i].content.trimEnd()} "
            val widthToReach = cells.subList(0, i + 1).sumOf { it.width } * singleCellWidth

            sb.append(content)
            currentWidth += widthInPixels(content)

            while (currentWidth + SPACE_WIDTH < widthToReach) {
                sb.append(" ")
                currentWidth += SPACE_WIDTH
            }
        }

        return sb.toString()
    }

}

private class Cell(val width: Int, val content: String)

internal class ListBuilder(columns: Int, private val list: List<String>) {

    private val lines = mutableListOf("")
    private val columnWidth: Int = CHAT_WINDOW_WIDTH / columns.coerceAtLeast(1)

    fun create(): List<String> {
        list.forEach { add(it) }
        return lines
    }

    private fun add(string: String) {
        val element = "$string "
        val lastLine = lines.last()
        val elementWidth = widthInPixels(element)
        val lineWidth = widthInPixels(lastLine)

        if (lineWidth + elementWidth < CHAT_WINDOW_WIDTH) {
            val line = padString(lastLine + element)
            lines[lines.lastIndex] = line
            return
        }

        lines.add(padString(string))
    }

    private fun padString(string: String): String {
        val width = widthInPixels(string)
        val spanningCols = width / columnWidth + 1
        val padding = spanningCols * columnWidth - width
        return string + " ".repeat(padding / SPACE_WIDTH)
    }

}