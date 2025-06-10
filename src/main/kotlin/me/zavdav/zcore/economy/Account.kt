package me.zavdav.zcore.economy

import me.zavdav.zcore.player.OfflinePlayer
import java.math.BigDecimal
import java.math.RoundingMode

/** Represents an account that is owned by a player. */
sealed interface Account {

    /** The owner of this account. */
    var owner: OfflinePlayer

    /** This account's current balance. */
    var balance: BigDecimal

    /** Determines how far this account can be overdrawn. */
    var overdrawLimit: BigDecimal

    /**
     * Tries to set the balance to an [amount].
     * Returns `true` if the overdraw limit would not be exceeded.
     */
    fun set(amount: BigDecimal): Boolean {
        val newBalance = amount.setScale(10, RoundingMode.DOWN)
        if (newBalance < -overdrawLimit) return false
        balance = newBalance
        return true
    }

    /** Adds an [amount] to the current balance. */
    fun add(amount: BigDecimal) {
        if (amount < BigDecimal.ZERO)
            throw IllegalArgumentException("Invalid amount: $amount")

        balance += amount.setScale(10, RoundingMode.DOWN)
    }

    /**
     * Tries to subtract an [amount] from the current balance.
     * Returns `true` if the overdraw limit would not be exceeded.
     */
    fun subtract(amount: BigDecimal): Boolean {
        if (amount < BigDecimal.ZERO)
            throw IllegalArgumentException("Invalid amount: $amount")

        return set(balance - amount.setScale(10, RoundingMode.DOWN))
    }

    /**
     * Tries to multiply the current balance with a [factor].
     * Returns `true` if the overdraw limit would not be exceeded.
     */
    fun multiply(factor: BigDecimal): Boolean {
        if (factor < BigDecimal.ZERO)
            throw IllegalArgumentException("Invalid factor: $factor")

        return set(balance * factor.setScale(10, RoundingMode.DOWN))
    }

    /**
     * Tries to divide the current balance by a [divisor].
     * Returns `true` if the overdraw limit would not be exceeded.
     */
    fun divide(divisor: BigDecimal): Boolean {
        if (divisor <= BigDecimal.ZERO)
            throw IllegalArgumentException("Invalid divisor: $divisor")

        return set(balance.divide(divisor.setScale(10, RoundingMode.DOWN), RoundingMode.DOWN))
    }

    /**
     * Tries to transfer an [amount] from this account to another [account].
     * Returns `true` if the overdraw limit would not be exceeded.
     */
    fun transfer(amount: BigDecimal, account: Account): Boolean {
        if (amount < BigDecimal.ZERO)
            throw IllegalArgumentException("Invalid amount: $amount")

        if (!subtract(amount)) return false
        account.add(amount)
        return true
    }

}