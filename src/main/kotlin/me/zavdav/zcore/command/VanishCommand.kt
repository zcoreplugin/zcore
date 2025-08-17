package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.CorePlayer
import me.zavdav.zcore.player.core
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

internal val vanishCommand = command(
    "vanish",
    "Toggles a player's vanish status",
    "zcore.vanish"
) {
    runs {
        val source = requirePlayer()
        doVanish(source)
    }
    playerArgument("player") {
        requiresPermission("zcore.vanish.other")
        runs {
            val player: CorePlayer by this
            doVanish(player)
        }
    }
}

private fun CommandContext<CommandSender>.doVanish(target: CorePlayer) {
    val source = this.source
    val self = source is Player && source.core() == target
    val isVanished = !target.data.isVanished
    target.data.isVanished = isVanished

    if (isVanished) {
        source.sendMessage(local("command.vanish.enabled", target.name))
        if (!self) target.sendMessage(local("command.vanish.enabled", target.name))
    } else {
        source.sendMessage(local("command.vanish.disabled", target.name))
        if (!self) target.sendMessage(local("command.vanish.disabled", target.name))
    }
}