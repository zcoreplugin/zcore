package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.inventory.InventoryView
import me.zavdav.zcore.player.CorePlayer
import org.bukkit.command.CommandSender

internal val invseeCommand = command(
    "invsee",
    "Views a player's inventory",
    "zcore.invsee"
) {
    playerArgument("player") {
        runs {
            val player: CorePlayer by this
            doInvsee(player)
        }
    }
}

private fun CommandContext<CommandSender>.doInvsee(target: CorePlayer) {
    val source = requirePlayer()
    InventoryView(source, target).open()
}