package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.ZCore
import me.zavdav.zcore.command.event.WarpSetEvent
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender

internal val setwarpCommand = command(
    "setwarp",
    "Sets a warp at your current location",
    "zcore.setwarp"
) {
    stringArgument("name") {
        runs {
            val name: String by this
            doSetWarp(name)
        }
    }
}

private fun CommandContext<CommandSender>.doSetWarp(warpName: String) {
    val source = requirePlayer()
    if (!warpName.matches(Regex("[a-zA-Z0-9_-]+")))
        throw TranslatableException("command.setwarp.illegal", warpName)

    val existingWarp = ZCore.getWarp(warpName)
    if (existingWarp == null) {
        if (!WarpSetEvent(source, warpName).call()) return
        ZCore.setWarp(warpName, source.location)
        source.sendMessage(local("command.setwarp", warpName))
    } else {
        throw TranslatableException("command.setwarp.exists", existingWarp.name)
    }
}