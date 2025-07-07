package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.util.formatDuration
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender

internal val seenCommand = command(
    "seen",
    "Shows when a player was last online.",
    "/seen [<player>]",
    "zcore.seen"
) {
    runs {
        val source = requirePlayer()
        doSeen(source.data)
    }
    offlinePlayerArgument("target") {
        runs {
            val target: OfflinePlayer by this
            doSeen(target)
        }
    }
}

private fun CommandContext<CommandSender>.doSeen(target: OfflinePlayer) {
    if (target.isOnline) {
        val duration = formatDuration(System.currentTimeMillis() - target.lastJoin)
        source.sendMessage(local("command.seen.online", target.name, duration))
    } else {
        val duration = formatDuration(System.currentTimeMillis() - target.lastActivity)
        source.sendMessage(local("command.seen.offline", target.name, duration))
    }
}