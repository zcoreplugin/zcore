package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.CorePlayer
import me.zavdav.zcore.player.core
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

internal val godCommand = command(
    "god",
    arrayOf("godmode"),
    "Toggles a player's invincibility",
    "zcore.god"
) {
    runs {
        val source = requirePlayer()
        doGod(source)
    }
    playerArgument("player") {
        requiresPermission("zcore.god.other")
        runs {
            val player: CorePlayer by this
            doGod(player)
        }
    }
}

private fun CommandContext<CommandSender>.doGod(target: CorePlayer) {
    val source = this.source
    val self = source is Player && source.core() == target
    val isInvincible = !target.data.isInvincible
    target.data.isInvincible = isInvincible

    if (isInvincible) {
        source.sendMessage(local("command.god.enabled", target.name))
        if (!self) target.sendMessage(local("command.god.enabled", target.name))
    } else {
        source.sendMessage(local("command.god.disabled", target.name))
        if (!self) target.sendMessage(local("command.god.disabled", target.name))
    }
}