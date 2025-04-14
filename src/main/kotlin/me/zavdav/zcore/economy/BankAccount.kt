package me.zavdav.zcore.economy

import me.zavdav.zcore.internal.util.checkAndPut
import me.zavdav.zcore.internal.util.checkAndRemove
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

    private val _users = mutableMapOf<OfflineUser, Role>()

    override var owner: OfflineUser = owner
        set(value) {
            val prevOwner = field
            field = value
            addUser(prevOwner)
            // Set the previous owner's role to MANAGER
            setUserRole(prevOwner, Role.MANAGER)
        }

    /** The bank account's UUID. */
    val uuid: UUID = uuid

    /** The bank account's name. */
    var name: String = name

    /** A map of users that can access the bank account together with their role. */
    val users: Map<OfflineUser, Role> get() = _users.plus(owner to Role.OWNER)

    /**
     * Adds a [user] to the bank account.
     * Returns `false` if the user is already a member of the bank.
     */
    fun addUser(user: OfflineUser): Boolean =
        if (user == owner) false else _users.checkAndPut(user, Role.DEFAULT)

    /**
     * Removes a [user] from the bank account.
     * Returns `false` if the user is not a member of the bank.
     */
    fun removeUser(user: OfflineUser): Boolean {
        if (user == owner) {
            throw IllegalArgumentException("Cannot remove owner of the bank account")
        }
        return _users.checkAndRemove(user)
    }

    /** Gets the role of the specified [user], or `null` if the user is not a member of the bank. */
    fun getUserRole(user: OfflineUser): Role? {
        if (user == owner) return Role.OWNER
        return _users[user]
    }

    /** Sets the [role] of the specified [user]. */
    fun setUserRole(user: OfflineUser, role: Role) {
        if (user == owner || role == Role.OWNER) {
            throw IllegalArgumentException("Use the 'owner' property to set the bank's owner")
        }
        if (user !in _users) return
        _users[user] = role
    }

    /**
     * Tries to transfer an [amount] from the account to another [account].
     * Returns `false` if the [overdrawLimit] would be exceeded by doing so.
     * @throws [BankTransactionException] if the account is a [UserAccount]
     * and belongs to a user that is not a member of the bank.
     */
    override fun transfer(amount: BigDecimal, account: EconomyAccount): Boolean {
        if (account is UserAccount && _users.containsKey(account.owner)) {
            throw BankTransactionException(this, account.owner, amount)
        }
        return super.transfer(amount, account)
    }

    /** Represents the roles users of the bank account can have. */
    enum class Role {

        /** The owner of the bank account. Has every permission. */
        OWNER,

        /** A manager of the bank account. Can do everything except renaming or deleting the account. */
        MANAGER,

        /** The default role. Can only deposit money into the account. */
        DEFAULT
    }

}