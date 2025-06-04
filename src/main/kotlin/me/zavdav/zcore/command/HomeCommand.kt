package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.util.tl
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.CommandSender

internal val homeCommand = command(
    "home",
    arrayOf("h"),
    "Teleports you to a home.",
    "/home <name>",
    "zcore.home"
) {
    stringArgument("homeName", StringType.SINGLE_WORD) {
        runs(permission) {
            val source = requirePlayer()
            val homeName: String by this
            doHome(source.data, homeName)
        }
    }
    offlinePlayerArgument("target") {
        stringArgument("homeName", StringType.SINGLE_WORD) {
            runs(permission) {
                val target: OfflinePlayer by this
                val homeName: String by this
                doHome(target, homeName)
            }
        }
    }
}

private fun CommandContext<CommandSender>.doHome(target: OfflinePlayer, homeName: String) {
    val source = requirePlayer()
    val self = source.data.uuid == target.uuid
    if (!self) require("zcore.home.other")

    val home = target.getHome(homeName)
    if (home != null) {
        val location = Location(Bukkit.getWorld(home.world), home.x, home.y, home.z, home.yaw, home.pitch)
        if (!source.safelyTeleport(location)) throw TranslatableException("command.home.unsafeLocation")
        if (self)
            source.sendMessage(tl("command.home.success", home.name))
        else
            source.sendMessage(tl("command.home.success.other", target.name, home.name))
    } else {
        throw TranslatableException("command.home.doesNotExist")
    }
}