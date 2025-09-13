package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.ZCore
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender
import java.math.BigDecimal
import java.math.RoundingMode

internal val ecoCommand = command(
    "eco",
    arrayOf("economy"),
    "Changes a player's balance",
    "zcore.eco"
) {
    literal("set") {
        offlinePlayerArgument("player") {
            bigDecimalArgument("amount") {
                runs {
                    val player: OfflinePlayer by this
                    val amount: BigDecimal by this
                    doEcoSet(player, amount)
                }
            }
        }
    }
    literal("give") {
        offlinePlayerArgument("player") {
            bigDecimalArgument("amount") {
                runs {
                    val player: OfflinePlayer by this
                    val amount: BigDecimal by this
                    doEcoGive(player, amount)
                }
            }
        }
    }
    literal("take") {
        offlinePlayerArgument("player") {
            bigDecimalArgument("amount") {
                runs {
                    val player: OfflinePlayer by this
                    val amount: BigDecimal by this
                    doEcoTake(player, amount)
                }
            }
        }
    }
}

private fun CommandContext<CommandSender>.doEcoSet(target: OfflinePlayer, amount: BigDecimal) {
    val roundedAmount = amount.setScale(2, RoundingMode.DOWN)
    if (target.account.set(roundedAmount)) {
        source.sendMessage(local("command.eco.set", target.name, ZCore.formatCurrency(roundedAmount)))
    } else {
        throw TranslatableException("command.eco.negative")
    }
}

private fun CommandContext<CommandSender>.doEcoGive(target: OfflinePlayer, amount: BigDecimal) {
    val roundedAmount = amount.setScale(2, RoundingMode.DOWN)
    if (roundedAmount <= BigDecimal.ZERO)
        throw TranslatableException("command.invalidAmount", roundedAmount)

    target.account.add(roundedAmount)
    source.sendMessage(local("command.eco.give", ZCore.formatCurrency(roundedAmount), target.name))
}

private fun CommandContext<CommandSender>.doEcoTake(target: OfflinePlayer, amount: BigDecimal) {
    val roundedAmount = amount.setScale(2, RoundingMode.DOWN)
    if (roundedAmount <= BigDecimal.ZERO)
        throw TranslatableException("command.invalidAmount", roundedAmount)

    if (target.account.subtract(roundedAmount)) {
        source.sendMessage(local("command.eco.take", ZCore.formatCurrency(roundedAmount), target.name))
    } else {
        throw TranslatableException("command.eco.negative")
    }
}