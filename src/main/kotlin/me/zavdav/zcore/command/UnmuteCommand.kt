package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.punishment.MuteList
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender

internal val unmuteCommand = command(
    "unmute",
    "Unmutes a player",
    "zcore.unmute"
) {
    offlinePlayerArgument("player") {
        runs {
            val player: OfflinePlayer by this
            doUnmute(player)
        }
    }
}

private fun CommandContext<CommandSender>.doUnmute(target: OfflinePlayer) {
    if (MuteList.pardonMute(target)) {
        source.sendMessage(local("command.unmute", target.name))
    } else {
        throw TranslatableException("command.unmute.notMuted", target.name)
    }
}