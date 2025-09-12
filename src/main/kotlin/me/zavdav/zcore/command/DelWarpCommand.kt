package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.ZCore
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender

internal val delwarpCommand = command(
    "delwarp",
    "Deletes a warp",
    "zcore.delwarp"
) {
    stringArgument("warp") {
        runs {
            val warp: String by this
            doDelWarp(warp)
        }
    }
}

private fun CommandContext<CommandSender>.doDelWarp(warpName: String) {
    val existingWarp = ZCore.deleteWarp(warpName)
    if (existingWarp != null) {
        source.sendMessage(local("command.delwarp", existingWarp.name))
    } else {
        throw TranslatableException("command.delwarp.unknown", warpName)
    }
}
