package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.teleportTo
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

internal val tphereCommand = command(
    "tphere",
    "Teleports a player to you",
    "zcore.tphere"
) {
    playerArgument("player") {
        runs {
            val player: Player by this
            doTpHere(player)
        }
    }
}

private fun CommandContext<CommandSender>.doTpHere(target: Player) {
    val source = requirePlayer()
    target.teleportTo(source)
    source.sendMessage(local("command.tp.player", target.name, source.name))
}