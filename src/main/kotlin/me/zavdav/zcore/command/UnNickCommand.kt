package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.CorePlayer
import me.zavdav.zcore.player.core
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

internal val unnickCommand = command(
    "unnick",
    "Resets a player's nickname",
    "zcore.unnick"
) {
    runs {
        val source = requirePlayer()
        doUnNick(source)
    }
    playerArgument("player") {
        requiresPermission("zcore.unnick.other")
        runs {
            val player: CorePlayer by this
            doUnNick(player)
        }
    }
}

private fun CommandContext<CommandSender>.doUnNick(target: CorePlayer) {
    val source = this.source
    val self = source is Player && source.core() == target

    target.data.nickname = null
    source.sendMessage(local("command.unnick", target.name))
    if (!self) target.sendMessage(local("command.unnick", target.name))
}