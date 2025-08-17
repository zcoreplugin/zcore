package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.ZCore
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
            doSetwarp(name)
        }
    }
}

private fun CommandContext<CommandSender>.doSetwarp(warpName: String) {
    val source = requirePlayer()
    if (!warpName.matches(Regex("[a-zA-Z0-9_-]+")))
        throw TranslatableException("command.setwarp.illegal", warpName)

    val existingWarp = ZCore.setWarp(warpName, source.location)
    if (existingWarp == null) {
        source.sendMessage(local("command.setwarp", warpName))
    } else {
        throw TranslatableException("command.setwarp.exists", existingWarp.name)
    }
}