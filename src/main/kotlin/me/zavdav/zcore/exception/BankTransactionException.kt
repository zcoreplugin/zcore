package me.zavdav.zcore.exception

import me.zavdav.zcore.economy.BankAccount
import me.zavdav.zcore.user.OfflineUser
import java.math.BigDecimal

/**
 * Thrown when a bank account tries to transfer money
 * to a user that is not a member of the bank.
 */
class BankTransactionException(
    val bankAccount: BankAccount,
    val user: OfflineUser,
    val amount: BigDecimal
) : RuntimeException()