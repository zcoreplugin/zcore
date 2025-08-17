package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.CorePlayer
import org.bukkit.command.CommandSender

internal val afkCommand = command(
    "afk",
    "Toggles a player's AFK status",
    "zcore.afk"
) {
    runs {
        val source = requirePlayer()
        doAfk(source)
    }
    playerArgument("player") {
        requiresPermission("zcore.afk.other")
        runs {
            val player: CorePlayer by this
            doAfk(player)
        }
    }
}

private fun CommandContext<CommandSender>.doAfk(target: CorePlayer) {
    if (target.isAfk) target.updateActivity() else target.setInactive()
}