package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.CorePlayer
import me.zavdav.zcore.player.core
import me.zavdav.zcore.util.colored
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
            doNick(source, nickname)
        }
    }
    playerArgument("player") {
        requiresPermission("zcore.nick.other")
        stringArgument("nickname") {
            runs {
                val player: CorePlayer by this
                val nickname: String by this
                doNick(player, nickname)
            }
        }
    }
}

private fun CommandContext<CommandSender>.doNick(target: CorePlayer, nickname: String) {
    val source = this.source
    val self = source is Player && source.core() == target

    var finalNickname = nickname
    if (source.hasPermission("zcore.nick.color"))
        finalNickname = finalNickname.colored()

    if (ChatColor.stripColor(finalNickname).isEmpty())
        throw TranslatableException("command.nick.empty")

    target.data.nickname = finalNickname
    target.base.displayName = target.displayName
    source.sendMessage(local("command.nick", target.name, target.displayName))
    if (!self) target.sendMessage(local("command.nick", target.name, target.displayName))
}