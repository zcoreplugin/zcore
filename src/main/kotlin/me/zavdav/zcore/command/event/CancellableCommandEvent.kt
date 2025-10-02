package me.zavdav.zcore.command.event

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.event.Cancellable
import org.bukkit.event.Event

open class CancellableCommandEvent<S : CommandSender>(val source: S) : Event("CancellableCommandEvent"), Cancellable {

    private var isCancelled: Boolean = false

    override fun isCancelled(): Boolean = isCancelled

    override fun setCancelled(cancel: Boolean) {
        throw UnsupportedOperationException("Use cancel(String) and provide an error message to cancel this event")
    }

    fun cancel(message: String) {
        if (isCancelled) return
        isCancelled = true
        source.sendMessage(message)
    }

    fun call(): Boolean {
        Bukkit.getPluginManager().callEvent(this)
        return !isCancelled
    }

}