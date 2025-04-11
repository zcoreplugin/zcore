package me.zavdav.zcore.statistic

import me.zavdav.zcore.user.OfflineUser
import me.zavdav.zcore.util.enumMap
import org.bukkit.Material
import org.bukkit.entity.CreatureType
import java.math.BigDecimal

/** Represents the statistics of a user. */
class UserStatistics(val user: OfflineUser) {

    // Backing fields
    internal val _blocksPlaced = enumMap<Material, Long>()
    internal val _blocksBroken = enumMap<Material, Long>()
    internal val _itemsDropped = enumMap<Material, Long>()
    internal val _userKills = mutableMapOf<OfflineUser, Long>()
    internal val _mobKills = enumMap<CreatureType, Long>()

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

}