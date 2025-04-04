package me.zavdav.zcore.api.economy

import me.zavdav.zcore.api.user.OfflineUser
import java.util.UUID

/** Represents an economy bank account that is owned by a single user. */
interface BankAccount : EconomyAccount<OfflineUser> {

    /** The bank account's UUID. */
    val uuid: UUID

    /** The bank account's name. */
    val name: String

    /** A list of users that manage the bank account. */
    val managers: Set<OfflineUser>

    /** A list of users that have access to the bank account. */
    val members: Set<OfflineUser>

}