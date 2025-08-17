package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.ZCore
import me.zavdav.zcore.economy.BankAccount
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.player.core
import me.zavdav.zcore.util.alignText
import me.zavdav.zcore.util.line
import me.zavdav.zcore.util.local
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.math.BigDecimal
import java.math.RoundingMode

internal val bankCommand = command(
    "bank",
    "Manages player banks",
    "zcore.bank"
) {
    literal("create") {
        stringArgument("name") {
            runs {
                val name: String by this
                doBankCreate(name)
            }
        }
    }
    literal("delete") {
        stringArgument("bank") {
            runs {
                val bank: String by this
                doBankDelete(bank)
            }
        }
    }
    stringArgument("bank") {
        runs {
            val bank: String by this
            doBankInfo(bank)
        }
        literal("deposit") {
            bigDecimalArgument("amount") {
                runs {
                    val bank: String by this
                    val amount: BigDecimal by this
                    doBankDeposit(bank, amount)
                }
            }
        }
        literal("withdraw") {
            bigDecimalArgument("amount") {
                runs {
                    val bank: String by this
                    val amount: BigDecimal by this
                    doBankWithdraw(bank, amount)
                }
            }
        }
        literal("add") {
            offlinePlayerArgument("player") {
                runs {
                    val bank: String by this
                    val player: OfflinePlayer by this
                    doBankAdd(bank, player)
                }
            }
        }
        literal("remove") {
            offlinePlayerArgument("player") {
                runs {
                    val bank: String by this
                    val player: OfflinePlayer by this
                    doBankRemove(bank, player)
                }
            }
        }
    }
}

private fun CommandContext<CommandSender>.doBankInfo(bankName: String) {
    val bank = ZCore.getBank(bankName) ?: throw TranslatableException("command.bank.unknown", bankName)
    source.sendMessage(local("command.bank.info", bank.name))
    source.sendMessage(line(ChatColor.GRAY))

    val info = listOf(
        local("command.bank.info.owner") to bank.owner.name,
        local("command.bank.info.balance") to ZCore.formatCurrency(bank.balance),
        local("command.bank.info.maxOverdraw") to ZCore.formatCurrency(bank.overdrawLimit)
    )

    info.forEach { (key, value) ->
        source.sendMessage(alignText(key to 1, "${ChatColor.GREEN}$value" to 1))
    }

    source.sendMessage(local("command.bank.info.members"))
    source.sendMessage(line(ChatColor.GRAY))
    val list = bank.members.sortedWith { p1, p2 -> p1.name.compareTo(p2.name, true) }
    if (list.isEmpty()) return
    list.forEach { source.sendMessage(local("command.bank.info.members.line", it.name)) }
}

private fun CommandContext<CommandSender>.doBankCreate(bankName: String) {
    val source = requirePlayer()
    if (ZCore.createBank(bankName, source.data) != null) {
        source.sendMessage(local("command.bank.create", bankName))
    } else {
        throw TranslatableException("command.bank.create.exists", bankName)
    }
}

private fun CommandContext<CommandSender>.doBankDelete(bankName: String) {
    val bank = ZCore.getBank(bankName) ?: throw TranslatableException("command.bank.unknown", bankName)
    authorizeOwner(bank, source)
    bank.transfer(bank.balance, bank.owner.account)
    bank.delete()
    source.sendMessage(local("command.bank.delete", bank.name))
}

private fun CommandContext<CommandSender>.doBankDeposit(bankName: String, amount: BigDecimal) {
    val source = requirePlayer()
    val bank = ZCore.getBank(bankName) ?: throw TranslatableException("command.bank.unknown", bankName)
    val roundedAmount = amount.setScale(2, RoundingMode.DOWN)

    if (roundedAmount <= BigDecimal.ZERO)
        throw TranslatableException("command.invalidAmount", roundedAmount)

    if (source.data.account.transfer(roundedAmount, bank)) {
        source.sendMessage(local("command.bank.deposit", ZCore.formatCurrency(roundedAmount), bank.name))
    } else {
        throw TranslatableException("command.bank.overdraw", ZCore.formatCurrency(bank.overdrawLimit))
    }
}

private fun CommandContext<CommandSender>.doBankWithdraw(bankName: String, amount: BigDecimal) {
    val source = requirePlayer()
    val bank = ZCore.getBank(bankName) ?: throw TranslatableException("command.bank.unknown", bankName)
    val roundedAmount = amount.setScale(2, RoundingMode.DOWN)
    authorizeMember(bank, source)

    if (roundedAmount <= BigDecimal.ZERO)
        throw TranslatableException("command.invalidAmount", roundedAmount)

    if (bank.transfer(roundedAmount, source.data.account)) {
        source.sendMessage(local("command.bank.withdraw", ZCore.formatCurrency(roundedAmount), bank.name))
    } else {
        throw TranslatableException("command.bank.overdraw", ZCore.formatCurrency(bank.overdrawLimit))
    }
}

private fun CommandContext<CommandSender>.doBankAdd(bankName: String, target: OfflinePlayer) {
    val bank = ZCore.getBank(bankName) ?: throw TranslatableException("command.bank.unknown", bankName)
    authorizeOwner(bank, source)
    if (bank.addPlayer(target)) {
        source.sendMessage(local("command.bank.add", target.name, bank.name))
        val player = ZCore.getPlayer(target.uuid)
        player?.sendMessage(local("command.bank.add", target.name, bank.name))
    } else {
        throw TranslatableException("command.bank.add.alreadyMember", target.name, bank.name)
    }
}

private fun CommandContext<CommandSender>.doBankRemove(bankName: String, target: OfflinePlayer) {
    val bank = ZCore.getBank(bankName) ?: throw TranslatableException("command.bank.unknown", bankName)
    authorizeOwner(bank, source)

    if (target == bank.owner)
        throw TranslatableException("command.bank.remove.owner", bank.name)

    if (bank.removePlayer(target)) {
        source.sendMessage(local("command.bank.remove", target.name, bank.name))
        val player = ZCore.getPlayer(target.uuid)
        player?.sendMessage(local("command.bank.remove", target.name, bank.name))
    } else {
        throw TranslatableException("command.bank.remove.notMember", target.name, bank.name)
    }
}

private fun authorizeMember(bank: BankAccount, source: CommandSender) {
    if (source !is Player || source.hasPermission("zcore.bank.op"))
        return

    val player = source.core()
    if (player.data !in bank.members && player.data != bank.owner)
        throw TranslatableException("command.bank.action.member", bank.name)
}

private fun authorizeOwner(bank: BankAccount, source: CommandSender) {
    if (source !is Player || source.hasPermission("zcore.bank.op"))
        return

    val player = source.core()
    if (player.data != bank.owner)
        throw TranslatableException("command.bank.action.owner", bank.name)
}