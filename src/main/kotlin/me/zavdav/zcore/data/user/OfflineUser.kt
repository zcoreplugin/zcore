package me.zavdav.zcore.data.user

import me.zavdav.zcore.data.economy.BankAccount
import me.zavdav.zcore.data.economy.UserAccount
import me.zavdav.zcore.event.UsernameChangeEvent
import me.zavdav.zcore.util.addIfAbsent
import me.zavdav.zcore.util.enumMap
import me.zavdav.zcore.data.location.NamedLocation
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.CreatureType
import java.math.BigDecimal
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
    internal val _blocksPlaced = enumMap<Material, Long>()
    internal val _blocksBroken = enumMap<Material, Long>()
    internal val _itemsDropped = enumMap<Material, Long>()
    internal val _userKills = mutableMapOf<OfflineUser, Long>()
    internal val _mobKills = enumMap<CreatureType, Long>()

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
    var invincible = false

    /** Determines if the user is vanished. */
    var vanished = false

    /** Determines if the user can see chat messages. */
    var chatEnabled = true

    /** Determines if the user can see social interactions by others. */
    var socialspy = false

    /** The user's playtime in milliseconds. */
    var playtime: Long = 0
        internal set

    /** A map of how many times the user has placed a type of block. */
    val blocksPlaced: Map<Material, Long> get() = _blocksPlaced

    /** The total amount of blocks the user has placed. */
    val totalBlocksPlaced: Long
        get() = _blocksPlaced.values.sum()

    /** A map of how many times the user has broken a type of block. */
    val blocksBroken: Map<Material, Long> get() = _blocksBroken

    /** The total amount of blocks the user has broken. */
    val totalBlocksBroken: Long
        get() = _blocksBroken.values.sum()

    /** A map of how many times the user has dropped a type of item. */
    val itemsDropped: Map<Material, Long> get() = _itemsDropped

    /** The total amount of items the user has dropped. */
    val totalItemsDropped: Long
        get() = _itemsDropped.values.sum()

    /** The total amount of blocks the user has traveled. */
    var blocksTraveled: BigDecimal = BigDecimal.ZERO
        internal set

    /** The total amount of damage the user has dealt to entities. */
    var damageDealt: Long = 0
        internal set

    /** The total amount of damage the user has taken. */
    var damageTaken: Long = 0
        internal set

    /** A map of how many times the user has killed another user. */
    val userKills: Map<OfflineUser, Long> get() = _userKills

    /** The total amount of times the user has killed other users. */
    val totalUserKills: Long
        get() = _userKills.values.sum()

    /** A map of how many times the user has killed a type of mob. */
    val mobKills: Map<CreatureType, Long> get() = _mobKills

    /** The total amount of mobs the user has killed. */
    val totalMobKills: Long
        get() = _mobKills.values.sum()

    /** The total amount of times the user has perished. */
    var deaths: Long = 0
        internal set

    /** Gets the location of a home by its [name], or `null` if no home with this name exists. */
    fun getHome(name: String): NamedLocation? = _homes[name.lowercase()]

    /**
     * Sets a new home with a [name] and a [location].
     * Returns `null`, or the home with this name if it already exists.
     */
    fun setHome(name: String, location: Location): NamedLocation? =
        _homes.putIfAbsent(name.lowercase(), NamedLocation(name, location))

    /**
     * Deletes the home with the specified [name].
     * Returns the home that was deleted, or `null` if no home with this name exists.
     */
    fun deleteHome(name: String): NamedLocation? =
        _homes.remove(name.lowercase())

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
        _ignoredUsers.addIfAbsent(user)

    /**
     * Removes the specified [user] from being ignored for the user.
     * Returns `false` if the user is not ignored.
     */
    fun removeIgnoredUser(user: OfflineUser): Boolean =
        _ignoredUsers.remove(user)

}