package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.ZCore
import me.zavdav.zcore.economy.BankAccount
import me.zavdav.zcore.player.CorePlayer
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.player.core
import me.zavdav.zcore.util.PagingList
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
    literal("list") {
        runs {
            val source = requirePlayer()
            doBankList(source.data, 1)
        }
        intArgument("page") {
            runs {
                val source = requirePlayer()
                val page: Int by this
                doBankList(source.data, page)
            }
        }
        offlinePlayerArgument("player") {
            runs {
                val player: OfflinePlayer by this
                doBankList(player, 1)
            }
            intArgument("page") {
                runs {
                    val player: OfflinePlayer by this
                    val page: Int by this
                    doBankList(player, page)
                }
            }
        }
    }
    literal("info") {
        bankArgument("bank") {
            runs {
                val bank: BankAccount by this
                doBankInfo(bank)
            }
        }
    }
    literal("members") {
        bankArgument("bank") {
            runs {
                val bank: BankAccount by this
                doBankMembers(bank, 1)
            }
            intArgument("page") {
                runs {
                    val bank: BankAccount by this
                    val page: Int by this
                    doBankMembers(bank, page)
                }
            }
        }
    }
    literal("create") {
        stringArgument("name") {
            runs {
                val name: String by this
                doBankCreate(name)
            }
        }
    }
    literal("delete") {
        bankArgument("bank") {
            runs {
                val bank: BankAccount by this
                doBankDelete(bank)
            }
        }
    }
    literal("deposit") {
        bigDecimalArgument("amount") {
            bankArgument("bank") {
                runs {
                    val amount: BigDecimal by this
                    val bank: BankAccount by this
                    doBankDeposit(amount, bank)
                }
            }
        }
    }
    literal("withdraw") {
        bigDecimalArgument("amount") {
            bankArgument("bank") {
                runs {
                    val amount: BigDecimal by this
                    val bank: BankAccount by this
                    doBankWithdraw(amount, bank)
                }
            }
        }
    }
    literal("invite") {
        playerArgument("player") {
            bankArgument("bank") {
                runs {
                    val player: CorePlayer by this
                    val bank: BankAccount by this
                    doBankInvite(player, bank)
                }
            }
        }
    }
    literal("kick") {
        offlinePlayerArgument("player") {
            bankArgument("bank") {
                runs {
                    val player: OfflinePlayer by this
                    val bank: BankAccount by this
                    doBankKick(player, bank)
                }
            }
        }
    }
    literal("join") {
        bankArgument("bank") {
            runs {
                val bank: BankAccount by this
                doBankJoin(bank)
            }
        }
    }
    literal("leave") {
        bankArgument("bank") {
            runs {
                val bank: BankAccount by this
                doBankLeave(bank)
            }
        }
    }
}

private fun CommandContext<CommandSender>.doBankList(target: OfflinePlayer, page: Int) {
    val banks = ZCore.bankAccounts
        .filter { target == it.owner || target in it.members }
        .sortedWith { b1, b2 -> b1.name.compareTo(b2.name, true) }
    val list = PagingList(banks, 10)
    if (list.isEmpty())
        throw TranslatableException("command.bank.list.none", target.name)

    val index = page.coerceIn(1..list.pages()) - 1
    source.sendMessage(local("command.bank.list", target.name, index + 1, list.pages()))
    source.sendMessage(line(ChatColor.GRAY))
    list.page(index).forEach {
        if (target == it.owner) {
            source.sendMessage(local("command.bank.list.owner", it.name))
        } else {
            source.sendMessage(local("command.bank.list.member", it.name))
        }
    }
}

private fun CommandContext<CommandSender>.doBankInfo(bank: BankAccount) {
    source.sendMessage(local("command.bank.info", bank.name))
    source.sendMessage(line(ChatColor.GRAY))
    source.sendMessage(alignText(local("command.bank.info.owner") to 1, bank.owner.name to 1))
    source.sendMessage(alignText(local("command.bank.info.balance") to 1, ZCore.formatCurrency(bank.balance) to 1))
    source.sendMessage(alignText(local("command.bank.info.members") to 1, bank.members.count() to 1))
}

private fun CommandContext<CommandSender>.doBankMembers(bank: BankAccount, page: Int) {
    val members = bank.members.sortedWith { p1, p2 -> p1.name.compareTo(p2.name, true) }
    val list = PagingList(members, 10)
    if (list.isEmpty())
        throw TranslatableException("command.bank.members.none", bank.name)

    val index = page.coerceIn(1..list.pages()) - 1
    source.sendMessage(local("command.bank.members", bank.name, index + 1, list.pages()))
    source.sendMessage(line(ChatColor.GRAY))
    list.page(index).forEach {
        source.sendMessage(local("command.bank.members.line", it.name))
    }
}

private fun CommandContext<CommandSender>.doBankCreate(name: String) {
    val source = requirePlayer()
    if (!name.matches(Regex("[a-zA-Z0-9_-]+")))
        throw TranslatableException("command.bank.create.illegal", name)

    val existingBank = ZCore.getBank(name)
    if (existingBank == null) {
        ZCore.createBank(name, source.data)
        source.sendMessage(local("command.bank.create", name))
    } else {
        throw TranslatableException("command.bank.create.exists", existingBank.name)
    }
}

private fun CommandContext<CommandSender>.doBankDelete(bank: BankAccount) {
    checkIsOwner(source, bank)
    bank.transfer(bank.balance, bank.owner.account)
    bank.delete()
    source.sendMessage(local("command.bank.delete", bank.name))
}

private fun CommandContext<CommandSender>.doBankDeposit(amount: BigDecimal, bank: BankAccount) {
    val source = requirePlayer()
    checkIsMember(source, bank)
    val finalAmount = roundAmount(amount)

    if (source.data.account.transfer(finalAmount, bank)) {
        source.sendMessage(local("command.bank.deposit", ZCore.formatCurrency(finalAmount), bank.name))
    } else {
        throw TranslatableException("command.bank.insufficient")
    }
}

private fun CommandContext<CommandSender>.doBankWithdraw(amount: BigDecimal, bank: BankAccount) {
    val source = requirePlayer()
    checkIsMember(source, bank)
    val finalAmount = roundAmount(amount)

    if (bank.transfer(finalAmount, source.data.account)) {
        source.sendMessage(local("command.bank.withdraw", ZCore.formatCurrency(finalAmount), bank.name))
    } else {
        throw TranslatableException("command.bank.insufficient")
    }
}

private fun CommandContext<CommandSender>.doBankInvite(target: CorePlayer, bank: BankAccount) {
    val source = requirePlayer()
    checkIsOwner(source, bank)

    if (target.data == bank.owner || target.data in bank.members)
        throw TranslatableException("command.bank.invite.alreadyMember", target.name, bank.name)

    if (bank !in target.bankInvites) {
        target.bankInvites.put(bank, source)
        source.sendMessage(local("command.bank.invite", target.name, bank.name))
        target.sendMessage(local("command.bank.invite.incoming", bank.name))
        target.sendMessage(local("command.bank.invite.use", bank.name))
    } else {
        source.sendMessage(local("command.bank.invite.alreadyInvited", target.name, bank.name))
    }
}

private fun CommandContext<CommandSender>.doBankKick(target: OfflinePlayer, bank: BankAccount) {
    checkIsOwner(source, bank)
    if (target == bank.owner)
        throw TranslatableException("command.bank.kick.owner", target.name, bank.name)

    if (bank.removePlayer(target)) {
        source.sendMessage(local("command.bank.kick", target.name, bank.name))
    } else {
        throw TranslatableException("command.bank.kick.notMember", target.name, bank.name)
    }
}

private fun CommandContext<CommandSender>.doBankJoin(bank: BankAccount) {
    val source = requirePlayer()
    val inviter = source.bankInvites.remove(bank)
    if (inviter != null) {
        bank.addPlayer(source.data)
        source.sendMessage(local("command.bank.join", bank.name))
        inviter.sendMessage(local("command.bank.join.incoming", source.name, bank.name))
    } else {
        throw TranslatableException("command.bank.join.noInvite", bank.name)
    }
}

private fun CommandContext<CommandSender>.doBankLeave(bank: BankAccount) {
    val source = requirePlayer()
    if (source.data == bank.owner)
        throw TranslatableException("command.bank.leave.owner", bank.name)

    if (bank.removePlayer(source.data)) {
        source.sendMessage(local("command.bank.leave", bank.name))
    } else {
        throw TranslatableException("command.bank.leave.notMember", bank.name)
    }
}

private fun roundAmount(amount: BigDecimal): BigDecimal {
    val roundedAmount = amount.setScale(2, RoundingMode.DOWN)
    if (roundedAmount <= BigDecimal.ZERO)
        throw TranslatableException("command.invalidAmount", roundedAmount)

    return roundedAmount
}

private fun checkIsOwner(source: CommandSender, bank: BankAccount) {
    if (source !is Player || source.hasPermission("zcore.bank.op"))
        return

    val player = source.core()
    if (player.data != bank.owner)
        throw TranslatableException("command.bank.action.owner", bank.name)
}

private fun checkIsMember(source: CommandSender, bank: BankAccount) {
    if (source !is Player || source.hasPermission("zcore.bank.op"))
        return

    val player = source.core()
    if (player.data !in bank.members && player.data != bank.owner)
        throw TranslatableException("command.bank.action.member", bank.name)
}