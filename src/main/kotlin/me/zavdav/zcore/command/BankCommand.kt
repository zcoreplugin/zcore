package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.ZCore
import me.zavdav.zcore.economy.BankAccount
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.player.core
import me.zavdav.zcore.util.PagedList
import me.zavdav.zcore.util.PagedTable
import me.zavdav.zcore.util.line
import me.zavdav.zcore.util.local
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.math.BigDecimal
import java.math.RoundingMode

internal val bankCommand = command(
    "bank",
    "Root command of the bank system.",
    "/bank",
    "zcore.bank"
) {
    literal("create") {
        stringArgument("bankName") {
            runs {
                val bankName: String by this
                doBankCreate(bankName)
            }
        }
    }
    literal("delete") {
        stringArgument("bankName") {
            runs {
                val bankName: String by this
                doBankDelete(bankName)
            }
        }
    }
    stringArgument("bankName") {
        runs {
            val bankName: String by this
            doBankInfo(bankName)
        }
        literal("deposit") {
            bigDecimalArgument("amount") {
                runs {
                    val bankName: String by this
                    val amount: BigDecimal by this
                    doBankDeposit(bankName, amount)
                }
            }
        }
        literal("withdraw") {
            bigDecimalArgument("amount") {
                runs {
                    val bankName: String by this
                    val amount: BigDecimal by this
                    doBankWithdraw(bankName, amount)
                }
            }
        }
        literal("add") {
            offlinePlayerArgument("target") {
                runs {
                    val bankName: String by this
                    val target: OfflinePlayer by this
                    doBankAdd(bankName, target)
                }
            }
        }
        literal("remove") {
            offlinePlayerArgument("target") {
                runs {
                    val bankName: String by this
                    val target: OfflinePlayer by this
                    doBankRemove(bankName, target)
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
        local("command.bank.info.maxOverdraw") to ZCore.formatCurrency(bank.overdrawLimit),
        local("command.bank.info.members") to ""
    )

    val table = PagedTable(info) { _, (key, value) ->
        arrayOf(key to 1, "${ChatColor.GREEN}$value" to 1)
    }

    table.print(0, source)
    source.sendMessage(line(ChatColor.GRAY))

    val members = bank.members.map { it.name }
    val list = PagedList(members, Int.MAX_VALUE, 4)
    if (list.pages() == 0) return
    list.print(0, source, ChatColor.GREEN)
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
    if (source !is Player || source.isOp && source.hasPermission("zcore.bank.op"))
        return

    val player = source.core()
    if (player.data !in bank.members && player.data != bank.owner)
        throw TranslatableException("command.bank.action.member", bank.name)
}

private fun authorizeOwner(bank: BankAccount, source: CommandSender) {
    if (source !is Player || source.isOp && source.hasPermission("zcore.bank.op"))
        return

    val player = source.core()
    if (player.data != bank.owner)
        throw TranslatableException("command.bank.action.owner", bank.name)
}