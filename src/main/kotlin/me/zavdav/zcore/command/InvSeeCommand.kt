package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.inventory.InventoryView
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

internal val invseeCommand = command(
    "invsee",
    "Views a player's inventory",
    "zcore.invsee"
) {
    playerArgument("player") {
        runs {
            val player: Player by this
            doInvSee(player)
        }
    }
}

private fun CommandContext<CommandSender>.doInvSee(target: Player) {
    val source = requirePlayer()
    InventoryView(source, target).open()
}