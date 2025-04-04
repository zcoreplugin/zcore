package me.zavdav.zcore.api.user

import org.bukkit.entity.Player

/** Represents a user that is currently online. A `User` is a superset of an [OfflineUser]. */
interface User : OfflineUser {

    /** The [Player] associated with this user. */
    val player: Player

    /** Views the inventory of a [player] and enables the [UserState.INVSEE] state. */
    fun viewPlayerInventory(player: Player)

    /** Restores the user's original inventory and disables the [UserState.INVSEE] state. */
    fun restoreInventory()

}