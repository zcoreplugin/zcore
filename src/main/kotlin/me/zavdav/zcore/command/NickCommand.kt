package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.ZCore
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.util.colored
import me.zavdav.zcore.util.computeNickname
import me.zavdav.zcore.util.local
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

internal val nickCommand = command(
    "nick",
    "Changes a player's nickname",
    "zcore.nick"
) {
    stringArgument("nickname") {
        runs {
            val source = requirePlayer()
            val nickname: String by this
            doNick(source.data, nickname)
        }
    }
    offlinePlayerArgument("player") {
        requiresPermission("zcore.nick.other")
        stringArgument("nickname") {
            runs {
                val player: OfflinePlayer by this
                val nickname: String by this
                doNick(player, nickname)
            }
        }
    }
}

private fun CommandContext<CommandSender>.doNick(target: OfflinePlayer, nickname: String) {
    val source = this.source
    val self = source is Player && source.uniqueId == target.uuid

    var finalNickname = nickname
    if (source.hasPermission("zcore.nick.color"))
        finalNickname = finalNickname.colored()

    if (ChatColor.stripColor(finalNickname).isEmpty())
        throw TranslatableException("command.nick.empty")

    target.nickname = finalNickname
    source.sendMessage(local("command.nick", target.name, computeNickname(target)))

    val onlineTarget = ZCore.getPlayer(target.uuid)
    if (onlineTarget != null) {
        onlineTarget.base.displayName = onlineTarget.displayName
        if (!self) onlineTarget.sendMessage(local("command.nick", target.name, onlineTarget.displayName))
    }
}