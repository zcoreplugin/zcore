package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.command.event.HomeSetEvent
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender

internal val sethomeCommand = command(
    "sethome",
    arrayOf("sh"),
    "Sets a home at your current location",
    "zcore.sethome"
) {
    stringArgument("name") {
        runs {
            val source = requirePlayer()
            val name: String by this
            doSetHome(source.data, name)
        }
    }
    offlinePlayerArgument("player") {
        requiresPermission("zcore.sethome.other")
        stringArgument("name") {
            runs {
                val player: OfflinePlayer by this
                val name: String by this
                doSetHome(player, name)
            }
        }
    }
}

private fun CommandContext<CommandSender>.doSetHome(target: OfflinePlayer, homeName: String) {
    val source = requirePlayer()
    if (!homeName.matches(Regex("[a-zA-Z0-9_-]+")))
        throw TranslatableException("command.sethome.illegal", homeName)

    val existingHome = target.getHome(homeName)
    if (existingHome == null) {
        if (!HomeSetEvent(source, target, homeName).call()) return
        target.setHome(homeName, source.location)
        source.sendMessage(local("command.sethome", target.name, homeName))
    } else {
        throw TranslatableException("command.sethome.exists", target.name, existingHome.name)
    }
}