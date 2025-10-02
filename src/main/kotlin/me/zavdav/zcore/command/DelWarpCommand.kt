package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.ZCore
import me.zavdav.zcore.command.event.WarpDeleteEvent
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
    val warp = ZCore.getWarp(warpName)
    if (warp != null) {
        if (!WarpDeleteEvent(source, warp).call()) return
        warp.delete()
        source.sendMessage(local("command.delwarp", warp.name))
    } else {
        throw TranslatableException("command.delwarp.unknown", warpName)
    }
}
