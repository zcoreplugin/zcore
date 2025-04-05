package me.zavdav.zcore.api.economy

import me.zavdav.zcore.api.user.OfflineUser
import java.util.UUID

/** Represents an economy bank account that is owned by a user. */
interface BankAccount : EconomyAccount {

    /** The bank account's UUID. */
    val uuid: UUID

    /** The bank account's name. */
    var name: String

    /** A map of users that can access the bank account together with their role. */
    val users: Map<OfflineUser, Role>

    /** Adds a [user] to the bank account. */
    fun addUser(user: OfflineUser)

    /** Removes a [user] from the bank account. */
    fun removeUser(user: OfflineUser)

    /** Gets the role of the specified [user]. */
    fun getUserRole(user: OfflineUser): Role

    /** Sets the [role] of the specified [user]. */
    fun setUserRole(user: OfflineUser, role: Role)

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