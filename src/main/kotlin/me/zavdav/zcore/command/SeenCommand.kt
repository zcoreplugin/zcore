package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.ZCore
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender

internal val seenCommand = command(
    "seen",
    "Shows when a player was last online",
    "zcore.seen"
) {
    runs {
        val source = requirePlayer()
        doSeen(source.data)
    }
    offlinePlayerArgument("player") {
        runs {
            val player: OfflinePlayer by this
            doSeen(player)
        }
    }
}

private fun CommandContext<CommandSender>.doSeen(target: OfflinePlayer) {
    if (target.isOnline) {
        val duration = ZCore.formatDuration(System.currentTimeMillis() - target.lastJoin)
        source.sendMessage(local("command.seen.online", target.name, duration))
    } else {
        val duration = ZCore.formatDuration(System.currentTimeMillis() - target.lastActivity)
        source.sendMessage(local("command.seen.offline", target.name, duration))
    }
}