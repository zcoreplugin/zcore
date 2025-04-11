package me.zavdav.zcore.economy

import me.zavdav.zcore.ZCore.Api.SYSTEM_USER
import me.zavdav.zcore.event.EconomyTransactionEvent
import me.zavdav.zcore.user.OfflineUser
import org.bukkit.Bukkit
import java.math.BigDecimal
import java.math.RoundingMode

/** Represents an economy account that is owned by a user. */
sealed class EconomyAccount(
    owner: OfflineUser,
    balance: BigDecimal = BigDecimal.ZERO,
    overdrawLimit: BigDecimal = BigDecimal.ZERO
) {

    /** The account's owner. */
    open var owner: OfflineUser = owner

    /** The account's current balance. */
    var balance: BigDecimal = balance
        set(value) {
            if (value < -overdrawLimit) return
            // Set to 10 decimal places
            field = value.setScale(10, RoundingMode.FLOOR)
        }

    /** Determines how far the account can be overdrawn. */
    var overdrawLimit: BigDecimal = overdrawLimit

    /** Adds an [amount] to the current balance. */
    fun add(amount: BigDecimal) {
        if (amount < BigDecimal.ZERO) {
            throw IllegalArgumentException("Cannot add negative amount")
        }
        SYSTEM_USER.account.transfer(amount, this)
    }

    /**
     * Tries to subtract an [amount] from the current balance.
     * Returns `false` if the [overdrawLimit] would be exceeded by doing so.
     */
    fun subtract(amount: BigDecimal): Boolean {
        if (amount < BigDecimal.ZERO) {
            throw IllegalArgumentException("Cannot subtract negative amount")
        }
        return transfer(amount, SYSTEM_USER.account)
    }

    /** Multiplies the current balance with a [factor]. */
    fun multiply(factor: BigDecimal) {
        if (factor < BigDecimal.ZERO) {
            throw IllegalArgumentException("Cannot multiply with negative factor")
        }

        val delta = balance * factor - balance
        if (delta < BigDecimal.ZERO) { // 0 <= factor < 1
            subtract(delta.abs())
        } else { // factor >= 1
            add(delta)
        }
    }

    /** Divides the current balance by a [divisor]. */
    fun divide(divisor: BigDecimal) {
        if (divisor <= BigDecimal.ZERO) {
            throw IllegalArgumentException("Cannot divide by 0 or negative divisor")
        }

        val delta = balance - balance / divisor
        if (delta < BigDecimal.ZERO) { // 0 < divisor < 1
            add(delta.abs())
        } else { // divisor >= 1
            subtract(delta)
        }
    }

    /**
     * Tries to transfer an [amount] from the account to another [account].
     * Returns `false` if the [overdrawLimit] would be exceeded by doing so.
     */
    open fun transfer(amount: BigDecimal, account: EconomyAccount): Boolean {
        if (amount < BigDecimal.ZERO) {
            throw IllegalArgumentException("Cannot transfer negative amount")
        }

        if (!hasEnough(amount)) return false
        balance -= amount
        account.balance += amount
        Bukkit.getPluginManager().callEvent(EconomyTransactionEvent(this, account, amount))
        return true
    }

    /**
     * Returns `true` if the [amount] could be subtracted from
     * the current balance without exceeding the [overdrawLimit].
     */
    fun hasEnough(amount: BigDecimal): Boolean {
        if (amount < BigDecimal.ZERO) {
            throw IllegalArgumentException("Cannot subtract negative amount")
        }
        return balance - amount < -overdrawLimit
    }

    /** Returns `true` if the current balance is above the [amount]. */
    fun hasOver(amount: BigDecimal) = balance > amount

    /** Returns `true` if the current balance is below the [amount]. */
    fun hasUnder(amount: BigDecimal) = balance < amount

}