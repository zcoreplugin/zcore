package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.ZCore
import me.zavdav.zcore.util.GridPage
import me.zavdav.zcore.util.tl
import org.bukkit.command.CommandSender

internal val warpsCommand = command(
    "warps",
    "Shows a list of warps.",
    "/warps [<page>]",
    "zcore.warps"
) {
    runs(permission) {
        doWarps(1)
    }
    intArgument("page") {
        runs(permission) {
            val page: Int by this
            doWarps(page)
        }
    }
}

private fun CommandContext<CommandSender>.doWarps(page: Int) {
    val pages = getPages()
    if (pages.isEmpty()) throw TranslatableException("command.warps.noWarps")

    val pageNumber = page.coerceIn(1..pages.size)
    val chatPage = pages[pageNumber - 1]
    chatPage.header = tl("command.warps.header", pageNumber, pages.size)
    chatPage.print(source)
}

private fun getPages(): List<GridPage> {
    val warps = ZCore.warps.map { it.name }.sorted()
    val pages = mutableListOf<GridPage>()
    if (warps.isEmpty()) return pages

    var currentPage = GridPage(10, 5)
    pages.add(currentPage)
    for (warp in warps) {
        if (currentPage.add(warp)) continue
        currentPage = GridPage(10, 5)
        pages.add(currentPage)
    }

    return pages
}