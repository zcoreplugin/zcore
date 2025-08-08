package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender

internal val movehomeCommand = command(
    "movehome",
    arrayOf("mh"),
    "Moves a home to your current location.",
    "/movehome <home>",
    "zcore.movehome"
) {
    stringArgument("homeName") {
        runs {
            val source = requirePlayer()
            val homeName: String by this
            doMovehome(source.data, homeName)
        }
    }
    offlinePlayerArgument("target") {
        stringArgument("homeName") {
            runs {
                val target: OfflinePlayer by this
                val homeName: String by this
                doMovehome(target, homeName)
            }
        }
    }
}

private fun CommandContext<CommandSender>.doMovehome(target: OfflinePlayer, homeName: String) {
    val source = requirePlayer()
    val self = source.data.uuid == target.uuid
    if (!self) require("zcore.movehome.other")

    val existingHome = target.moveHome(homeName, source.location)
    if (existingHome != null) {
        source.sendMessage(local("command.movehome", target.name, existingHome.name))
    } else {
        source.sendMessage(local("command.movehome.unknown", target.name, homeName))
    }
}