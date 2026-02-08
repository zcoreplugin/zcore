package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.isAfk
import me.zavdav.zcore.player.setInactive
import me.zavdav.zcore.player.updateActivity
import org.bukkit.command.CommandSender

internal val afkCommand = command(
    "afk",
    "Toggles a player's AFK status",
    "zcore.afk"
) {
    runs {
        doAfk()
    }
}

private fun CommandContext<CommandSender>.doAfk() {
    val source = requirePlayer()
    if (source.isAfk) source.updateActivity() else source.setInactive()
}