package me.zavdav.zcore.event

import me.zavdav.zcore.player.core
import me.zavdav.zcore.punishment.MuteList
import me.zavdav.zcore.util.formatDuration
import me.zavdav.zcore.util.tl
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChatEvent

class ChatListener : Listener {

    @EventHandler(priority = Event.Priority.Low, ignoreCancelled = true)
    fun onPlayerChat(event: PlayerChatEvent) {
        val player = event.player.core()
        val mute = MuteList.getActiveMute(player.data) ?: return
        val duration = mute.expiration?.let { it - System.currentTimeMillis() }

        event.isCancelled = true
        if (duration != null)
            player.sendMessage(tl("command.mute.temporary.message", formatDuration(duration), mute.reason))
        else
            player.sendMessage(tl("command.mute.permanent.message", mute.reason))
    }

}