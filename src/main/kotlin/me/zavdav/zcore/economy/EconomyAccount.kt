package me.zavdav.zcore.economy

import me.zavdav.zcore.ZCore.Api.SYSTEM_PLAYER
import me.zavdav.zcore.event.EconomyTransactionEvent
import me.zavdav.zcore.player.OfflinePlayer
import org.bukkit.Bukkit
import java.math.BigDecimal

/** Represents an account that is owned by a player. */
sealed interface EconomyAccount {

    /** The owner of this account. */
    var owner: OfflinePlayer

    /** This account's current balance. */
    var balance: BigDecimal

    /** Determines how far this account can be overdrawn. */
    var overdrawLimit: BigDecimal

    /** Adds an [amount] to the current balance. */
    fun add(amount: BigDecimal) {
        if (amount < BigDecimal.ZERO) {
            throw IllegalArgumentException("Cannot add negative amount")
        }
        SYSTEM_PLAYER.account.transfer(amount, this)
    }

    /**
     * Tries to subtract an [amount] from the current balance.
     * Returns `true` on success, `false` if the [overdrawLimit]
     * would be exceeded by doing so.
     */
    fun subtract(amount: BigDecimal): Boolean {
        if (amount < BigDecimal.ZERO) {
            throw IllegalArgumentException("Cannot subtract negative amount")
        }
        return transfer(amount, SYSTEM_PLAYER.account)
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
     * Tries to transfer an [amount] from this account to another [account].
     * Returns `true` on success, `false` if the [overdrawLimit] would be
     * exceeded by doing so.
     */
    fun transfer(amount: BigDecimal, account: EconomyAccount): Boolean {
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
     * Returns `true` if an [amount] could be subtracted from
     * the current balance without exceeding the [overdrawLimit].
     */
    fun hasEnough(amount: BigDecimal): Boolean {
        if (amount < BigDecimal.ZERO) {
            throw IllegalArgumentException("Cannot subtract negative amount")
        }
        return balance - amount < -overdrawLimit
    }

    /** Returns `true` if the current balance is above an [amount]. */
    fun hasOver(amount: BigDecimal) = balance > amount

    /** Returns `true` if the current balance is below an [amount]. */
    fun hasUnder(amount: BigDecimal) = balance < amount

}