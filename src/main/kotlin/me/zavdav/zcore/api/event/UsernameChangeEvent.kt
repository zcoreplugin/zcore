package me.zavdav.zcore.api.event

import me.zavdav.zcore.api.user.User
import org.bukkit.event.Event

/** Called when a user joins the server with a new username. */
class UsernameChangeEvent(

    /** The user that changed their username. */
    val user: User,

    /** The user's old username. */
    val oldName: String,

    /** The user's new username. */
    val newName: String

) : Event("UsernameChangeEvent")