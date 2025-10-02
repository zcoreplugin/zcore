package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.command.event.PlayerUnmuteEvent
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
            doUnMute(player)
        }
    }
}

private fun CommandContext<CommandSender>.doUnMute(target: OfflinePlayer) {
    if (MuteList.getActiveMute(target) != null) {
        if (!PlayerUnmuteEvent(source, target).call()) return
        MuteList.pardonMute(target)
        source.sendMessage(local("command.unmute", target.name))
    } else {
        throw TranslatableException("command.unmute.notMuted", target.name)
    }
}