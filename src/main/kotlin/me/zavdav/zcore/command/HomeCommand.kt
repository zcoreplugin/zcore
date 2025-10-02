package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.command.event.HomeTeleportEvent
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender

internal val homeCommand = command(
    "home",
    arrayOf("h"),
    "Teleports you to a player's home",
    "zcore.home"
) {
    stringArgument("home") {
        runs {
            val source = requirePlayer()
            val home: String by this
            doHome(source.data, home)
        }
    }
    offlinePlayerArgument("player") {
        requiresPermission("zcore.home.other")
        stringArgument("home") {
            runs {
                val player: OfflinePlayer by this
                val home: String by this
                doHome(player, home)
            }
        }
    }
}

private fun CommandContext<CommandSender>.doHome(target: OfflinePlayer, homeName: String) {
    val source = requirePlayer()
    val home = target.getHome(homeName)
    if (home == null)
        throw TranslatableException("command.home.unknown", target.name, homeName)

    if (!HomeTeleportEvent(source, home).call()) return
    val location = home.toBukkitLocation()
    if (source.safelyTeleport(location)) {
        source.sendMessage(local("command.home", target.name, home.name))
    } else {
        throw TranslatableException("command.home.unsafe")
    }
}