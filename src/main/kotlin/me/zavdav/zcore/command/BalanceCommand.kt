package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.ZCore
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.player.core
import me.zavdav.zcore.util.tl
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.math.RoundingMode

internal val balanceCommand = command(
    "balance",
    arrayOf("bal"),
    "Shows your current balance.",
    "/balance",
    "zcore.balance"
) {
    runs {
        val source = requirePlayer()
        doBalance(source.data)
    }
    offlinePlayerArgument("target") {
        runs {
            val target: OfflinePlayer by this
            doBalance(target)
        }
    }
}

private fun CommandContext<CommandSender>.doBalance(target: OfflinePlayer) {
    val source = this.source
    val self = source is Player && source.core().data.uuid == target.uuid
    if (!self) require("zcore.balance.other")

    val balance = target.account.balance.setScale(2, RoundingMode.DOWN)
    source.sendMessage(tl("command.balance.success", ZCore.formatCurrency(balance)))
}