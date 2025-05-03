package me.zavdav.zcore.data.user

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

/** Represents a user that is currently online. */
class User(id: EntityID<UUID>) : OfflineUser(id) {

    /** The [Player] associated with this user. */
    val bukkit: Player
        @JvmName("bukkit")
        get() = Bukkit.getServer().getPlayer(uuid)

    /** Determines if this user is AFK. */
    var afk = false

    /** Determines if this user is viewing another user's inventory. */
    var invsee = false

}