package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.ZCore
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender
import java.math.BigDecimal
import java.math.RoundingMode

internal val payCommand = command(
    "pay",
    "Sends money to another player.",
    "/pay <player> <amount>",
    "zcore.pay"
) {
    offlinePlayerArgument("target") {
        bigDecimalArgument("amount") {
            runs {
                val target: OfflinePlayer by this
                val amount: BigDecimal by this
                doPay(target, amount)
            }
        }
    }
}

private fun CommandContext<CommandSender>.doPay(target: OfflinePlayer, amount: BigDecimal) {
    val source = requirePlayer()
    val roundedAmount = amount.setScale(2, RoundingMode.DOWN)
    if (roundedAmount <= BigDecimal.ZERO)
        throw TranslatableException("command.invalidAmount", roundedAmount)

    if (source.data.account.transfer(roundedAmount, target.account)) {
        source.sendMessage(local("command.pay", ZCore.formatCurrency(roundedAmount), target.name))
        val player = ZCore.getPlayer(target.uuid)
        player?.sendMessage(local("command.pay.received", ZCore.formatCurrency(roundedAmount), source.name))
    } else {
        throw TranslatableException("command.pay.overdraw", ZCore.formatCurrency(source.data.account.overdrawLimit))
    }
}