package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.ZCore
import me.zavdav.zcore.command.event.PlayerUnnickEvent
import me.zavdav.zcore.player.OfflinePlayer
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
        doUnNick(source.data)
    }
    offlinePlayerArgument("player") {
        requiresPermission("zcore.unnick.other")
        runs {
            val player: OfflinePlayer by this
            doUnNick(player)
        }
    }
}

private fun CommandContext<CommandSender>.doUnNick(target: OfflinePlayer) {
    val source = this.source
    val self = source is Player && source.uniqueId == target.uuid

    if (!PlayerUnnickEvent(source, target).call()) return
    target.nickname = null
    source.sendMessage(local("command.unnick", target.name))

    val onlineTarget = ZCore.getPlayer(target.uuid)
    if (onlineTarget != null) {
        onlineTarget.base.displayName = onlineTarget.displayName
        if (!self) onlineTarget.sendMessage(local("command.unnick", target.name))
    }
}