package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.CorePlayer
import me.zavdav.zcore.player.core
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

internal val unvanishCommand = command(
    "unvanish",
    "Disables invisibility for a player",
    "zcore.unvanish"
) {
    runs {
        val source = requirePlayer()
        doUnVanish(source)
    }
    playerArgument("player") {
        requiresPermission("zcore.unvanish.other")
        runs {
            val player: CorePlayer by this
            doUnVanish(player)
        }
    }
}

private fun CommandContext<CommandSender>.doUnVanish(target: CorePlayer) {
    val source = this.source
    val self = source is Player && source.core() == target

    target.data.isVanished = false
    source.sendMessage(local("command.unvanish", target.name))
    if (!self) target.sendMessage(local("command.unvanish", target.name))
}