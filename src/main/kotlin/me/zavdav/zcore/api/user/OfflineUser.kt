package me.zavdav.zcore.api.user

import me.zavdav.zcore.api.economy.BankAccount
import me.zavdav.zcore.api.economy.UserAccount
import org.bukkit.Location
import java.util.UUID

/** Represents an offline user that has played on the server before. */
interface OfflineUser {

    /** The user's UUID. */
    val uuid: UUID

    /** The user's username. */
    val name: String

    /** The epoch millisecond of the user's first join. */
    val firstJoin: Long

    /** The epoch millisecond of the user's last join. */
    val lastJoin: Long

    /** The epoch millisecond of when the user was last online. */
    val lastOnline: Long

    /** The user's current playtime in milliseconds. */
    val playtime: Long

    /** The user's economy account where their balance is stored. */
    val account: UserAccount

    /** A list of bank accounts that are owned by the user. */
    val bankAccounts: List<BankAccount>

    /** A map of the user's homes. */
    val homes: Map<String, Location>

    /** The user's mail. */
    val mail: List<Pair<OfflineUser, String>>

    /** A list of users that the user has ignored. */
    val ignoredUsers: Set<OfflineUser>

    /** The user's nickname. Can be null if the user has no nickname. */
    var nickname: String?

    /** A map of the user's boolean states. */
    val states: Map<UserState, Boolean>

    /** Sets a [location] as a new home with a [name]. */
    fun setHome(name: String, location: Location)

    /** Deletes the home with the specified [name]. */
    fun deleteHome(name: String)

    /** Adds a [message] from a [source] to the user's mail. */
    fun addMail(source: OfflineUser, message: String)

    /** Clears all of the user's current mail. */
    fun clearMail()

    /** Sets whether the user has [ignored] the specified [user]. */
    fun setUserIgnored(user: OfflineUser, ignored: Boolean)

    /** Gets the current value for a [state] of the user. */
    fun getState(state: UserState): Boolean

    /** Sets the [value] for a [state] of the user. */
    fun setState(state: UserState, value: Boolean)

}