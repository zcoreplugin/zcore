package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.util.PagingList
import me.zavdav.zcore.util.line
import me.zavdav.zcore.util.local
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

internal val ipsCommand = command(
    "ips",
    "Shows a player's past IP addresses",
    "zcore.ips"
) {
    offlinePlayerArgument("player") {
        runs {
            val player: OfflinePlayer by this
            doIps(player, 1)
        }
        intArgument("page") {
            runs {
                val player: OfflinePlayer by this
                val page: Int by this
                doIps(player, page)
            }
        }
    }
}

private fun CommandContext<CommandSender>.doIps(target: OfflinePlayer, page: Int) {
    val addresses = target.ipAddresses
    val list = PagingList(addresses, 10)

    val index = page.coerceIn(1..list.pages()) - 1
    source.sendMessage(local("command.ips", target.name, index + 1, list.pages()))
    source.sendMessage(line(ChatColor.GRAY))
    list.page(index).forEach {
        source.sendMessage(local("command.ips.line", it.hostAddress))
    }
}