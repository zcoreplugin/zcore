package me.zavdav.zcore.user

import me.zavdav.zcore.economy.BankAccount
import me.zavdav.zcore.economy.UserAccount
import me.zavdav.zcore.event.UsernameChangeEvent
import me.zavdav.zcore.internal.util.checkAndAdd
import me.zavdav.zcore.internal.util.checkAndPut
import me.zavdav.zcore.internal.util.checkAndRemove
import me.zavdav.zcore.location.NamedLocation
import me.zavdav.zcore.statistic.UserStatistics
import org.bukkit.Bukkit
import org.bukkit.Location
import java.util.UUID

/** Represents an offline user that has played on the server before. */
sealed class OfflineUser(uuid: UUID, name: String) {

    internal var _name: String = name
        set(value) {
            if (this is User && field != value) {
                Bukkit.getPluginManager().callEvent(UsernameChangeEvent(this, field, value))
            }
            field = value
        }

    private val _bankAccounts = mutableListOf<BankAccount>()
    private val _homes = mutableMapOf<String, NamedLocation>()
    private val _mail = mutableListOf<Pair<OfflineUser, String>>()
    private val _ignoredUsers = mutableListOf<OfflineUser>()

    /** The user's UUID. */
    val uuid: UUID = uuid

    /** The user's username. */
    val name: String get() = _name

    /** The user's nickname. Can be `null` if the user has no nickname. */
    var nickname: String? = null

    /** The epoch millisecond of the user's first join. */
    var firstJoin: Long = 0
        internal set

    /** The epoch millisecond of the user's last join. */
    var lastJoin: Long = 0
        internal set

    /** The epoch millisecond of when the user was last online. */
    var lastOnline: Long = 0
        internal set

    /** The user's statistics. */
    val statistics = UserStatistics(this)

    /** The user's economy account where their balance is stored. */
    val account = UserAccount(this)

    /** A list of bank accounts that are owned by the user. */
    val bankAccounts: List<BankAccount> get() = _bankAccounts

    /** A map of the user's homes. */
    val homes: Map<String, NamedLocation> get() = _homes

    /** The user's mail. */
    val mail: List<Pair<OfflineUser, String>> get() = _mail

    /** A list of users that the user is ignoring. */
    val ignoredUsers: List<OfflineUser> get() = _ignoredUsers

    /** Determines if the user is invincible. */
    var isInvincible = false

    /** Determines if the user is vanished. */
    var isVanished = false

    /** Determines if the user can see chat messages. */
    var isChatEnabled = true

    /** Determines if the user can see social interactions by others. */
    var isSocialSpyEnabled = false

    /** Gets the location of a home by its [name], or `null` if no home with this name exists. */
    fun getHome(name: String): NamedLocation? = _homes[name.lowercase()]

    /**
     * Sets a new home with a [name] and a [location].
     * Returns `false` if a home with this name already exists.
     */
    fun setHome(name: String, location: Location): Boolean =
        _homes.checkAndPut(name.lowercase(), NamedLocation(name, location))

    /**
     * Deletes the home with the specified [name].
     * Returns `false` if no home with this name exists.
     */
    fun deleteHome(name: String): Boolean =
        _homes.checkAndRemove(name.lowercase())

    /** Adds a [message] from a [source] to the user's mail. */
    fun addMail(source: OfflineUser, message: String) {
        _mail.add(source to message)
    }

    /** Clears all of the user's current mail. */
    fun clearMail() = _mail.clear()

    /**
     * Sets the specified [user] as ignored for the user.
     * Returns `false` if the user is already ignored.
     */
    fun addIgnoredUser(user: OfflineUser): Boolean =
        _ignoredUsers.checkAndAdd(user)

    /**
     * Removes the specified [user] from being ignored for the user.
     * Returns `false` if the user is not ignored.
     */
    fun removeIgnoredUser(user: OfflineUser): Boolean =
        _ignoredUsers.checkAndRemove(user)

}