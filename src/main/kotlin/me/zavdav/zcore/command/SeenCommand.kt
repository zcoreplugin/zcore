package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.player.core
import me.zavdav.zcore.util.formatDuration
import me.zavdav.zcore.util.tl
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

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
    val source = this.source

    if (target.isOnline) {
        val duration = formatDuration(System.currentTimeMillis() - target.lastJoin)
        val self = source is Player && source.core().data.uuid == target.uuid

        if (self)
            source.sendMessage(tl("command.seen.online", duration))
        else
            source.sendMessage(tl("command.seen.online.other", target.name, duration))
    } else {
        val duration = formatDuration(System.currentTimeMillis() - target.lastActivity)
        source.sendMessage(tl("command.seen.offline", target.name, duration))
    }
}