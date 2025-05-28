package me.zavdav.zcore.economy

import me.zavdav.zcore.data.BankAccounts
import me.zavdav.zcore.data.BankMembers
import me.zavdav.zcore.player.OfflinePlayer
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import java.math.BigDecimal
import java.util.UUID

/** Represents a bank account that is owned by a player. */
class BankAccount(id: EntityID<UUID>) : EconomyAccount(id) {

    internal companion object : UUIDEntityClass<BankAccount>(BankAccounts) {
        fun new(
            name: String,
            owner: OfflinePlayer,
            balance: BigDecimal = BigDecimal.ZERO,
            overdrawLimit: BigDecimal = BigDecimal.ZERO
        ): BankAccount {
            val base = new(owner, balance, overdrawLimit)
            return new(base.id.value) {
                this.name = name
            }
        }
    }

    /** This bank account's UUID. */
    val uuid: UUID get() = id.value

    override var owner: OfflinePlayer get() = super.owner
        set(value) {
            val prevOwner = super.owner
            super.owner = value
            addPlayer(prevOwner)
        }

    /** This bank account's name. */
    var name: String by BankAccounts.name

    /** The players that can access this bank account. */
    val members by OfflinePlayer via BankMembers

    /**
     * Adds a [player] to this bank account.
     * Returns `true` on success, `false` if that player is already a member of this bank.
     */
    fun addPlayer(player: OfflinePlayer): Boolean {
        if (player == owner) return false
        val notExists = player !in members
        if (notExists) {
            BankMembers.insert {
                it[bank] = this@BankAccount.id
                it[this.player] = player.id
            }
        }
        return notExists
    }

    /**
     * Removes a [player] from this bank account.
     * Returns `true` on success, `false` if that player is not a member of this bank.
     */
    fun removePlayer(player: OfflinePlayer): Boolean {
        if (player == owner) {
            throw IllegalArgumentException("Cannot remove owner of the bank account")
        }
        val exists = player in members
        if (exists) {
            BankMembers.deleteWhere { (this.bank eq this@BankAccount.id) and (this.player eq player.id) }
        }
        return exists
    }

}