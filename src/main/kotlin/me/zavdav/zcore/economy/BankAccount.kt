package me.zavdav.zcore.economy

import me.zavdav.zcore.data.BankAccounts
import me.zavdav.zcore.data.BankMembers
import me.zavdav.zcore.player.OfflinePlayer
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.UUID

/** Represents a bank account that is owned by a player. */
class BankAccount private constructor(id: EntityID<UUID>) : UUIDEntity(id), EconomyAccount {

    companion object : UUIDEntityClass<BankAccount>(BankAccounts)

    /** This bank account's UUID. */
    val uuid: UUID get() = id.value

    /** This bank account's name. */
    var name: String by BankAccounts.name

    private var _owner by OfflinePlayer referencedOn BankAccounts.owner

    override var owner: OfflinePlayer
        get() = _owner
        set(value) {
            val prevOwner = _owner
            _owner = value
            addPlayer(prevOwner)
        }

    /** The players that can access this bank account. */
    val members by OfflinePlayer via BankMembers

    private var _balance: BigDecimal by BankAccounts.balance

    override var balance: BigDecimal
        get() = _balance
        set(value) {
            if (value < -overdrawLimit) return
            _balance = value.setScale(10, RoundingMode.FLOOR)
        }

    override var overdrawLimit: BigDecimal by BankAccounts.overdrawLimit

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