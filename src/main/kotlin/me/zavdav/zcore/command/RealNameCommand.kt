package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.core
import me.zavdav.zcore.util.local
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

internal val realnameCommand = command(
    "realname",
    arrayOf("whois"),
    "Shows the name of a player with a nickname",
    "zcore.realname"
) {
    stringArgument("nickname") {
        runs {
            val nickname: String by this
            doRealName(nickname)
        }
    }
}

private fun CommandContext<CommandSender>.doRealName(nickname: String) {
    val matches = Bukkit.getOnlinePlayers().filter {
        val nick = it.core().data.nickname
        nick != null && ChatColor.stripColor(nick).equals(nickname, true)
    }

    if (matches.isEmpty())
        throw TranslatableException("command.realname.none", nickname)

    matches.forEach { source.sendMessage(local("command.realname", it.core().displayName, it.name)) }
}