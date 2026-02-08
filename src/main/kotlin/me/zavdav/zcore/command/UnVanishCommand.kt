package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.command.event.VanishDisableEvent
import me.zavdav.zcore.player.data
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
            val player: Player by this
            doUnVanish(player)
        }
    }
}

private fun CommandContext<CommandSender>.doUnVanish(target: Player) {
    val source = this.source
    val self = source is Player && source == target

    if (!VanishDisableEvent(source, target).call()) return
    target.data.isVanished = false
    source.sendMessage(local("command.unvanish", target.name))
    if (!self) target.sendMessage(local("command.unvanish", target.name))
}