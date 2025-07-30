package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.inventory.InventoryView
import me.zavdav.zcore.player.CorePlayer
import org.bukkit.command.CommandSender

internal val invseeCommand = command(
    "invsee",
    "Views the inventory of a player.",
    "/invsee <player>",
    "zcore.invsee"
) {
    playerArgument("target") {
        runs {
            val target: CorePlayer by this
            doInvsee(target)
        }
    }
}

private fun CommandContext<CommandSender>.doInvsee(target: CorePlayer) {
    val source = requirePlayer()
    InventoryView(source, target).open()
}