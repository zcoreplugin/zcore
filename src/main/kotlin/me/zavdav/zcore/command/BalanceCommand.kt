package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.ZCore
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender
import java.math.RoundingMode

internal val balanceCommand = command(
    "balance",
    arrayOf("bal"),
    "Shows a player's balance",
    "zcore.balance"
) {
    runs {
        val source = requirePlayer()
        doBalance(source.data)
    }
    offlinePlayerArgument("player") {
        requiresPermission("zcore.balance.other")
        runs {
            val player: OfflinePlayer by this
            doBalance(player)
        }
    }
}

private fun CommandContext<CommandSender>.doBalance(target: OfflinePlayer) {
    val balance = target.account.balance.setScale(2, RoundingMode.DOWN)
    source.sendMessage(local("command.balance", target.name, ZCore.formatCurrency(balance)))
}