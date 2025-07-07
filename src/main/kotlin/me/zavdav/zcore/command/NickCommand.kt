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
    arrayOf("nickname"),
    "Changes a player's nickname.",
    "/nick [<player>] (<nickname>|reset)",
    "zcore.nick"
) {
    stringArgument("nickname") {
        runs {
            val source = requirePlayer()
            val nickname: String by this
            doNick(source, nickname)
        }
    }
    playerArgument("target") {
        stringArgument("nickname") {
            runs {
                val target: CorePlayer by this
                val nickname: String by this
                doNick(target, nickname)
            }
        }
    }
}

private fun CommandContext<CommandSender>.doNick(target: CorePlayer, nickname: String) {
    val source = this.source
    val self = source is Player && source.core() == target
    if (!self) require("zcore.nick.other")

    if (nickname.equals("reset", true) || nickname == target.name) {
        target.data.nickname = null
        source.sendMessage(local("command.nick.reset", target.name))
        if (!self) target.sendMessage(local("command.nick.reset", target.name))
        return
    }

    var finalNickname = nickname
    if (source.isOp || source.hasPermission("zcore.nick.color"))
        finalNickname = finalNickname.colored()

    if (ChatColor.stripColor(finalNickname).isEmpty())
        throw TranslatableException("command.nick.empty")

    target.data.nickname = finalNickname
    source.sendMessage(local("command.nick", target.name, target.displayName))
    if (!self) target.sendMessage(local("command.nick", target.name, target.displayName))
}