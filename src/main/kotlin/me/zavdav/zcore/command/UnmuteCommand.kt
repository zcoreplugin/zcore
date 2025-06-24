package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.punishment.MuteList
import me.zavdav.zcore.util.tl
import org.bukkit.command.CommandSender

internal val unmuteCommand = command(
    "unmute",
    "Unmutes a player.",
    "/unmute <player>",
    "zcore.unmute"
) {
    offlinePlayerArgument("target") {
        runs {
            val target: OfflinePlayer by this
            doUnmute(target)
        }
    }
}

private fun CommandContext<CommandSender>.doUnmute(target: OfflinePlayer) {
    if (MuteList.pardonMute(target))
        source.sendMessage(tl("command.unmute", target.name))
    else
        throw TranslatableException("command.unmute.notMuted")
}