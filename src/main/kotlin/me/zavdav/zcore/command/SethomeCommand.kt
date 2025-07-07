package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender

internal val sethomeCommand = command(
    "sethome",
    arrayOf("sh"),
    "Sets a home at your current location.",
    "/sethome <name>",
    "zcore.sethome"
) {
    stringArgument("homeName") {
        runs {
            val source = requirePlayer()
            val homeName: String by this
            doSethome(source.data, homeName)
        }
    }
    offlinePlayerArgument("target") {
        stringArgument("homeName") {
            runs {
                val target: OfflinePlayer by this
                val homeName: String by this
                doSethome(target, homeName)
            }
        }
    }
}

private fun CommandContext<CommandSender>.doSethome(target: OfflinePlayer, homeName: String) {
    val source = requirePlayer()
    val self = source.data.uuid == target.uuid
    if (!self) require("zcore.sethome.other")

    val allowedHomes = target.getPermissionValue("zcore.sethome.allowed", 1)
    if (allowedHomes < target.homes.count() + 1)
        throw TranslatableException("command.sethome.limit", target.name, allowedHomes)

    if (!homeName.matches(Regex("[a-zA-Z0-9_-]+")))
        throw TranslatableException("command.sethome.illegal", homeName)

    val existingHome = target.setHome(homeName, source.location)
    if (existingHome == null) {
        source.sendMessage(local("command.sethome", target.name, homeName))
    } else {
        throw TranslatableException("command.sethome.exists", target.name, existingHome.name)
    }
}