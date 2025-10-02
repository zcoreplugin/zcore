package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.command.event.HomeMoveEvent
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender

internal val movehomeCommand = command(
    "movehome",
    arrayOf("mh"),
    "Moves a player's home to your current location",
    "zcore.movehome"
) {
    stringArgument("home") {
        runs {
            val source = requirePlayer()
            val home: String by this
            doMoveHome(source.data, home)
        }
    }
    offlinePlayerArgument("player") {
        requiresPermission("zcore.movehome.other")
        stringArgument("home") {
            runs {
                val player: OfflinePlayer by this
                val home: String by this
                doMoveHome(player, home)
            }
        }
    }
}

private fun CommandContext<CommandSender>.doMoveHome(target: OfflinePlayer, homeName: String) {
    val source = requirePlayer()
    val existingHome = target.getHome(homeName)
    if (existingHome != null) {
        if (!HomeMoveEvent(source, existingHome, source.location).call()) return
        target.moveHome(homeName, source.location)
        source.sendMessage(local("command.movehome", target.name, existingHome.name))
    } else {
        source.sendMessage(local("command.movehome.unknown", target.name, homeName))
    }
}