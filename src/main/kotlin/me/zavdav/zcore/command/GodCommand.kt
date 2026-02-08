package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.command.event.InvincibilityEnableEvent
import me.zavdav.zcore.player.data
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

internal val godCommand = command(
    "god",
    "Enables invulnerability for a player",
    "zcore.god"
) {
    runs {
        val source = requirePlayer()
        doGod(source)
    }
    playerArgument("player") {
        requiresPermission("zcore.god.other")
        runs {
            val player: Player by this
            doGod(player)
        }
    }
}

private fun CommandContext<CommandSender>.doGod(target: Player) {
    val source = this.source
    val self = source is Player && source == target

    if (!InvincibilityEnableEvent(source, target).call()) return
    target.data.isInvincible = true
    source.sendMessage(local("command.god", target.name))
    if (!self) target.sendMessage(local("command.god", target.name))
}