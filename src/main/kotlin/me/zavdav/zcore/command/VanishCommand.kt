package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.command.event.VanishEnableEvent
import me.zavdav.zcore.player.data
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

internal val vanishCommand = command(
    "vanish",
    "Enables invisibility for a player",
    "zcore.vanish"
) {
    runs {
        val source = requirePlayer()
        doVanish(source)
    }
    playerArgument("player") {
        requiresPermission("zcore.vanish.other")
        runs {
            val player: Player by this
            doVanish(player)
        }
    }
}

private fun CommandContext<CommandSender>.doVanish(target: Player) {
    val source = this.source
    val self = source is Player && source == target

    if (!VanishEnableEvent(source, target).call()) return
    target.data.isVanished = true
    source.sendMessage(local("command.vanish", target.name))
    if (!self) target.sendMessage(local("command.vanish", target.name))
}