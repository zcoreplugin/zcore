package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.ZCore
import me.zavdav.zcore.economy.BankAccount
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.player.core
import me.zavdav.zcore.util.PageBuilder
import me.zavdav.zcore.util.tl
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

private fun CommandContext<CommandSender>.doBankCreate(bankName: String) {
    val source = requirePlayer()
    if (ZCore.createBank(bankName, source.data) != null)
        source.sendMessage(tl("command.bank.create", bankName))
    else
        throw TranslatableException("command.bank.create.alreadyExists")
}

private fun CommandContext<CommandSender>.doBankDelete(bankName: String) {
    val bank = ZCore.getBank(bankName) ?: throw TranslatableException("command.bank.doesNotExist")
    authorizeOwner(bank, source)
    bank.transfer(bank.balance, bank.owner.account)
    bank.delete()
    source.sendMessage(tl("command.bank.delete", bank.name))
}

private fun CommandContext<CommandSender>.doBankInfo(bankName: String) {
    val bank = ZCore.getBank(bankName) ?: throw TranslatableException("command.bank.doesNotExist")

    val builder = PageBuilder {
        header(tl("command.bank.info.header", bank.name))
        row {
            cell(1, tl("command.bank.info.owner"))
            cell(1, bank.owner.name)
        }
        row {
            cell(1, tl("command.bank.info.balance"))
            cell(1, ZCore.formatCurrency(bank.balance))
        }
        if (!bank.members.empty()) {
            row {
                cell(1, tl("command.bank.info.members"))
            }
            list(5, bank.members.map { it.name })
        }
    }

    val page = builder.create()
    page.print(source)
}

private fun CommandContext<CommandSender>.doBankDeposit(bankName: String, amount: BigDecimal) {
    val source = requirePlayer()
    val bank = ZCore.getBank(bankName) ?: throw TranslatableException("command.bank.doesNotExist")
    val roundedAmount = amount.setScale(2, RoundingMode.DOWN)

    if (roundedAmount <= BigDecimal.ZERO)
        throw TranslatableException("command.invalidAmount")

    if (source.data.account.transfer(roundedAmount, bank))
        source.sendMessage(tl("command.bank.deposit", ZCore.formatCurrency(roundedAmount), bank.name))
    else
        throw TranslatableException("command.bank.insufficientFunds")
}

private fun CommandContext<CommandSender>.doBankWithdraw(bankName: String, amount: BigDecimal) {
    val source = requirePlayer()
    val bank = ZCore.getBank(bankName) ?: throw TranslatableException("command.bank.doesNotExist")
    val roundedAmount = amount.setScale(2, RoundingMode.DOWN)
    authorizeMember(bank, source)

    if (roundedAmount <= BigDecimal.ZERO)
        throw TranslatableException("command.invalidAmount")

    if (bank.transfer(roundedAmount, source.data.account))
        source.sendMessage(tl("command.bank.withdraw", ZCore.formatCurrency(roundedAmount), bank.name))
    else
        throw TranslatableException("command.bank.insufficientFunds")
}

private fun CommandContext<CommandSender>.doBankAdd(bankName: String, target: OfflinePlayer) {
    val bank = ZCore.getBank(bankName) ?: throw TranslatableException("command.bank.doesNotExist")
    authorizeOwner(bank, source)
    if (bank.addPlayer(target)) {
        source.sendMessage(tl("command.bank.add", target.name, bank.name))
        val player = ZCore.getPlayer(target.uuid)
        player?.sendMessage(tl("bank.nowMember", bank.name))
    } else {
        throw TranslatableException("command.bank.add.alreadyMember")
    }
}

private fun CommandContext<CommandSender>.doBankRemove(bankName: String, target: OfflinePlayer) {
    val bank = ZCore.getBank(bankName) ?: throw TranslatableException("command.bank.doesNotExist")
    authorizeOwner(bank, source)

    if (target == bank.owner)
        throw TranslatableException("command.bank.remove.cannotRemoveOwner")

    if (bank.removePlayer(target)) {
        source.sendMessage(tl("command.bank.remove", target.name, bank.name))
        val player = ZCore.getPlayer(target.uuid)
        player?.sendMessage(tl("bank.noLongerMember", bank.name))
    } else {
        throw TranslatableException("command.bank.remove.notMember")
    }
}

private fun authorizeMember(bank: BankAccount, source: CommandSender) {
    if (source !is Player || source.isOp && source.hasPermission("zcore.bank.op"))
        return

    val player = source.core()
    if (player.data !in bank.members && player.data != bank.owner)
        throw TranslatableException("command.bank.notMember")
}

private fun authorizeOwner(bank: BankAccount, source: CommandSender) {
    if (source !is Player || source.isOp && source.hasPermission("zcore.bank.op"))
        return

    val player = source.core()
    if (player.data != bank.owner)
        throw TranslatableException("command.bank.notOwner")
}