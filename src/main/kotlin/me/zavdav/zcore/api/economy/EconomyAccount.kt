package me.zavdav.zcore.api.economy

import me.zavdav.zcore.api.user.OfflineUser
import java.math.BigDecimal

/** Represents an economy account that is owned by a user. */
interface EconomyAccount {

    /** The account's owner. */
    var owner: OfflineUser

    /** The account's current balance. */
    val balance: BigDecimal

    /** Determines how far the account can be overdrawn. */
    var overdrawLimit: BigDecimal

    /** Adds an [amount] to the current balance. */
    fun add(amount: BigDecimal)

    /**
     * Tries to subtract an [amount] from the current balance.
     * Returns false if the [overdrawLimit] would be exceeded by doing so.
     */
    fun subtract(amount: BigDecimal): Boolean

    /** Multiplies the current balance with a [factor]. */
    fun multiply(factor: BigDecimal)

    /** Divides the current balance by a [divisor]. */
    fun divide(divisor: BigDecimal)

    /**
     * Tries to transfer an [amount] from the account to another [account].
     * Returns false if the [overdrawLimit] would be exceeded by doing so.
     */
    fun transfer(amount: BigDecimal, account: EconomyAccount): Boolean

    /**
     * Returns true if the [amount] could be subtracted from
     * the current balance without exceeding the [overdrawLimit].
     */
    fun hasEnough(amount: BigDecimal): Boolean

    /** Returns true if the current balance is above the [amount]. */
    fun hasOver(amount: BigDecimal): Boolean

    /** Returns true if the current balance is below the [amount]. */
    fun hasUnder(amount: BigDecimal): Boolean

}