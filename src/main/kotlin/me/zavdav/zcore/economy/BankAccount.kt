package me.zavdav.zcore.economy

import me.zavdav.zcore.internal.util.addIfAbsent
import me.zavdav.zcore.user.OfflineUser
import java.math.BigDecimal
import java.util.UUID

/** Represents an economy bank account that is owned by a user. */
class BankAccount(
    uuid: UUID,
    name: String,
    owner: OfflineUser,
    balance: BigDecimal = BigDecimal.ZERO,
    overdrawLimit: BigDecimal = BigDecimal.ZERO
) : EconomyAccount(owner, balance, overdrawLimit) {

    private val _users = mutableListOf<OfflineUser>()

    override var owner: OfflineUser = owner
        set(value) {
            val prevOwner = field
            field = value
            addUser(prevOwner)
        }

    /** The bank account's UUID. */
    val uuid: UUID = uuid

    /** The bank account's name. */
    var name: String = name

    /** A list of users that can access the bank account. */
    val users: List<OfflineUser> get() = _users

    /**
     * Adds a [user] to the bank account.
     * Returns `false` if the user is already a member of the bank.
     */
    fun addUser(user: OfflineUser): Boolean =
        if (user == owner) false else _users.addIfAbsent(user)

    /**
     * Removes a [user] from the bank account.
     * Returns `false` if the user is not a member of the bank.
     */
    fun removeUser(user: OfflineUser): Boolean {
        if (user == owner) {
            throw IllegalArgumentException("Cannot remove owner of the bank account")
        }
        return _users.remove(user)
    }

    /**
     * Tries to transfer an [amount] from the account to another [account].
     * Returns `false` if the [overdrawLimit] would be exceeded by doing so.
     * @throws [BankTransactionException] if the account is a [UserAccount]
     * and belongs to a user that is not a member of the bank.
     */
    override fun transfer(amount: BigDecimal, account: EconomyAccount): Boolean {
        if (account is UserAccount && account.owner !in _users) {
            throw BankTransactionException(this, account.owner, amount)
        }
        return super.transfer(amount, account)
    }

}