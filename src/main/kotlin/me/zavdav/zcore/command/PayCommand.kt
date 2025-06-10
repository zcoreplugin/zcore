package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.ZCore
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.util.tl
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
        throw TranslatableException("command.invalidAmount")

    if (source.data.account.transfer(roundedAmount, target.account)) {
        source.sendMessage(tl("command.pay.success", target.name, ZCore.formatCurrency(roundedAmount)))
        val player = ZCore.getPlayer(target.uuid)
        player?.sendMessage(tl("pay.receivedPayment", ZCore.formatCurrency(roundedAmount), target.name))
    } else {
        throw TranslatableException("command.pay.insufficientFunds")
    }
}