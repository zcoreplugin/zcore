package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.CorePlayer
import me.zavdav.zcore.player.core
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

internal val ungodCommand = command(
    "ungod",
    "Disables invulnerability for a player",
    "zcore.ungod"
) {
    runs {
        val source = requirePlayer()
        doUnGod(source)
    }
    playerArgument("player") {
        requiresPermission("zcore.ungod.other")
        runs {
            val player: CorePlayer by this
            doUnGod(player)
        }
    }
}

private fun CommandContext<CommandSender>.doUnGod(target: CorePlayer) {
    val source = this.source
    val self = source is Player && source.core() == target
    target.data.isInvincible = false

    source.sendMessage(local("command.ungod", target.name))
    if (!self) target.sendMessage(local("command.ungod", target.name))
}