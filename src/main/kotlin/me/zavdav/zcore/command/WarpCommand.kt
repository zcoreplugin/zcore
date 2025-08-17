package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.ZCore
import me.zavdav.zcore.util.local
import me.zavdav.zcore.util.normalizedDirection
import org.bukkit.command.CommandSender

internal val warpCommand = command(
    "warp",
    "Teleports you to a warp",
    "zcore.warp"
) {
    stringArgument("warp") {
        runs {
            val warp: String by this
            doWarp(warp)
        }
    }
}

private fun CommandContext<CommandSender>.doWarp(warpName: String) {
    val source = requirePlayer()
    val warp = ZCore.getWarp(warpName)
    if (warp == null)
        throw TranslatableException("command.warp.unknown", warpName)

    val location = warp.toBukkitLocation().normalizedDirection()
    if (source.safelyTeleport(location)) {
        source.sendMessage(local("command.warp", warp.name))
    } else {
        throw TranslatableException("command.warp.unsafe")
    }
}