package me.zavdav.zcore.user

import me.zavdav.zcore.ZCore
import org.bukkit.entity.Player

/** The [OfflineUser] associated with this player. */
val Player.data: OfflineUser
    get() = ZCore.getOfflineUser(uniqueId)!!

/** Determines if this player is AFK. */
var Player.afk: Boolean
    get() = TODO()
    set(value) = TODO()

/** Determines if this player is viewing another player's inventory. */
var Player.invsee: Boolean
    get() = TODO()
    set(value) = TODO()