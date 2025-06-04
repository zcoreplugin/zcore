package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.util.tl
import org.bukkit.command.CommandSender

internal val sethomeCommand = command(
    "sethome",
    arrayOf("sh"),
    "Sets a home at your current location.",
    "/sethome <name>",
    "zcore.sethome"
) {
    stringArgument("homeName", StringType.SINGLE_WORD) {
        runs(permission) {
            val source = requirePlayer()
            val homeName: String by this
            doSethome(source.data, homeName)
        }
    }
    offlinePlayerArgument("target") {
        stringArgument("homeName", StringType.SINGLE_WORD) {
            runs(permission) {
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

    if (target.getPermissionValue("zcore.sethome.allowed", 1) < target.homes.count() + 1) {
        if (self)
            throw TranslatableException("command.sethome.limitReached")
        else
            throw TranslatableException("command.sethome.limitReached.other")
    }

    if (!homeName.matches(Regex("[a-zA-Z0-9_-]+"))) {
        throw TranslatableException("command.sethome.illegalName")
    }

    val location = source.location
    val existingHome = target.setHome(homeName, location)

    if (existingHome == null) {
        if (self)
            source.sendMessage(tl("command.sethome.success",
                homeName, location.world.name, location.blockX, location.blockY, location.blockZ
            ))
        else
            source.sendMessage(tl("command.sethome.success.other",
                target.name, homeName, location.world.name, location.blockX, location.blockY, location.blockZ
            ))
    } else {
        throw TranslatableException("command.sethome.alreadyExists", existingHome.name)
    }
}