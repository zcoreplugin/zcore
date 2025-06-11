package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.ZCore
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.util.tl
import org.bukkit.command.CommandSender
import java.math.BigDecimal
import java.math.RoundingMode

internal val ecoCommand = command(
    "eco",
    arrayOf("economy"),
    "Modifies the balance of players.",
    "/eco (set|give|take) <player> <amount>",
    "zcore.eco"
) {
    literal("set") {
        offlinePlayerArgument("target") {
            bigDecimalArgument("amount") {
                runs {
                    val target: OfflinePlayer by this
                    val amount: BigDecimal by this
                    doEcoSet(target, amount)
                }
            }
        }
    }
    literal("give") {
        offlinePlayerArgument("target") {
            bigDecimalArgument("amount") {
                runs {
                    val target: OfflinePlayer by this
                    val amount: BigDecimal by this
                    doEcoGive(target, amount)
                }
            }
        }
    }
    literal("take") {
        offlinePlayerArgument("target") {
            bigDecimalArgument("amount") {
                runs {
                    val target: OfflinePlayer by this
                    val amount: BigDecimal by this
                    doEcoTake(target, amount)
                }
            }
        }
    }
}

private fun CommandContext<CommandSender>.doEcoSet(target: OfflinePlayer, amount: BigDecimal) {
    val roundedAmount = amount.setScale(2, RoundingMode.DOWN)
    if (target.account.set(roundedAmount))
        source.sendMessage(tl("command.eco.set", target.name, ZCore.formatCurrency(roundedAmount)))
    else
        throw TranslatableException("command.eco.balanceTooLow")
}

private fun CommandContext<CommandSender>.doEcoGive(target: OfflinePlayer, amount: BigDecimal) {
    val roundedAmount = amount.setScale(2, RoundingMode.DOWN)

    if (roundedAmount <= BigDecimal.ZERO)
        throw TranslatableException("command.invalidAmount")

    target.account.add(roundedAmount)
    source.sendMessage(tl("command.eco.give", ZCore.formatCurrency(roundedAmount), target.name))
}

private fun CommandContext<CommandSender>.doEcoTake(target: OfflinePlayer, amount: BigDecimal) {
    val roundedAmount = amount.setScale(2, RoundingMode.DOWN)

    if (roundedAmount <= BigDecimal.ZERO)
        throw TranslatableException("command.invalidAmount")

    if (target.account.subtract(roundedAmount))
        source.sendMessage(tl("command.eco.take", ZCore.formatCurrency(roundedAmount), target.name))
    else
        throw TranslatableException("command.eco.balanceTooLow")
}