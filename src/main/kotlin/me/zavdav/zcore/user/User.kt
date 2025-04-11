package me.zavdav.zcore.user

import me.zavdav.zcore.event.UsernameChangeEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.UUID

/** Represents a user that is currently online. A `User` is a superset of an [OfflineUser]. */
class User(uuid: UUID) : OfflineUser(uuid) {

    override var _name: String?
        get() = super._name
        set(value) {
            val prevName = _name
            super._name = value ?: return
            if (prevName != null && prevName != value) {
                Bukkit.getPluginManager().callEvent(UsernameChangeEvent(this, prevName, value))
            }
        }

    /** The [Player] associated with this user. */
    val bukkit: Player
        @JvmName("bukkit")
        get() = Bukkit.getServer().getPlayer(uuid)

    /** Determines if the user is AFK. */
    var isAfk = false

    /** Determines if the user is viewing a player's inventory. */
    var isInvSeeEnabled = false

}