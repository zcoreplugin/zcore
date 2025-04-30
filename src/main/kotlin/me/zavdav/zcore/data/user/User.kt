package me.zavdav.zcore.data.user

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.UUID

/** Represents a user that is currently online. A `User` is a superset of an [OfflineUser]. */
class User(uuid: UUID, name: String) : OfflineUser(uuid, name) {

    /** The [Player] associated with this user. */
    val bukkit: Player
        @JvmName("bukkit")
        get() = Bukkit.getServer().getPlayer(uuid)

    /** Determines if the user is AFK. */
    var isAfk = false

    /** Determines if the user is viewing a player's inventory. */
    var isInvSeeEnabled = false

}