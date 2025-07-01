package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.CorePlayer
import me.zavdav.zcore.player.core
import me.zavdav.zcore.util.colored
import me.zavdav.zcore.util.tl
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
        if (self) {
            source.sendMessage(tl("command.nick.reset"))
        } else {
            source.sendMessage(tl("command.nick.reset.other", target.name))
            target.sendMessage(tl("command.nick.reset"))
        }
        return
    }

    var finalNickname = nickname
    if (source.isOp || source.hasPermission("zcore.nick.color"))
        finalNickname = finalNickname.colored()

    if (ChatColor.stripColor(finalNickname).isEmpty())
        throw TranslatableException("command.nick.cannotBeEmpty")

    target.data.nickname = finalNickname
    if (self) {
        source.sendMessage(tl("command.nick", target.displayName))
    } else {
        source.sendMessage(tl("command.nick.other", target.name, target.displayName))
        target.sendMessage(tl("command.nick", target.displayName))
    }
}