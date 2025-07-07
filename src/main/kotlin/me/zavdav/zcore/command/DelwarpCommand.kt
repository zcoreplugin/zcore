package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.ZCore
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender

internal val delwarpCommand = command(
    "delwarp",
    "Deletes a warp.",
    "/delwarp <name>",
    "zcore.delwarp"
) {
    stringArgument("warpName") {
        runs {
            val warpName: String by this
            doDelwarp(warpName)
        }
    }
}

fun CommandContext<CommandSender>.doDelwarp(warpName: String) {
    val existingWarp = ZCore.deleteWarp(warpName)
    if (existingWarp != null) {
        source.sendMessage(local("command.delwarp", existingWarp.name))
    } else {
        throw TranslatableException("command.delwarp.unknown", warpName)
    }
}
