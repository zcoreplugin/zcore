package me.zavdav.zcore.exception

import me.zavdav.zcore.economy.BankAccount
import me.zavdav.zcore.user.OfflineUser
import java.math.BigDecimal

/**
 * Thrown when a bank account tries to transfer money
 * to a user that is not a member of the bank.
 */
class BankTransactionException(

    /** The bank account that tried to transfer the money. */
    val bankAccount: BankAccount,

    /** The user who the bank account tried to transfer the money to. */
    val user: OfflineUser,

    /** The amount of money that was attempted to transfer. */
    val amount: BigDecimal

) : RuntimeException()