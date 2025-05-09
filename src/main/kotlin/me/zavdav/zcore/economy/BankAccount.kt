package me.zavdav.zcore.economy

import me.zavdav.zcore.data.BankAccountUsers
import me.zavdav.zcore.data.BankAccounts
import me.zavdav.zcore.user.OfflineUser
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import java.math.BigDecimal
import java.util.UUID

/** Represents a bank account that is owned by a user. */
class BankAccount(id: EntityID<UUID>) : EconomyAccount(id) {

    internal companion object : UUIDEntityClass<BankAccount>(BankAccounts) {
        fun new(
            name: String,
            owner: OfflineUser,
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

    override var owner: OfflineUser get() = super.owner
        set(value) {
            val prevOwner = super.owner
            super.owner = value
            addUser(prevOwner)
        }

    /** This bank account's name. */
    var name: String by BankAccounts.name

    /** The users that can access this bank account. */
    val users by OfflineUser via BankAccountUsers

    /**
     * Adds a [user] to this bank account.
     * Returns `true` on success, `false` if that user is already a member of this bank.
     */
    fun addUser(user: OfflineUser): Boolean {
        if (user == owner) return false
        val notExists = user !in users
        if (notExists) {
            BankAccountUsers.insert {
                it[bank] = this@BankAccount.id
                it[this.user] = user.id
            }
        }
        return notExists
    }

    /**
     * Removes a [user] from this bank account.
     * Returns `true` on success, `false` if that user is not a member of this bank.
     */
    fun removeUser(user: OfflineUser): Boolean {
        if (user == owner) {
            throw IllegalArgumentException("Cannot remove owner of the bank account")
        }
        val exists = user in users
        if (exists) {
            BankAccountUsers.deleteWhere { (this.bank eq this@BankAccount.id) and (this.user eq user.id) }
        }
        return exists
    }

}