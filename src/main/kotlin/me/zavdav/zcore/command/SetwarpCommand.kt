package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.ZCore
import me.zavdav.zcore.util.tl
import org.bukkit.command.CommandSender

internal val setwarpCommand = command(
    "setwarp",
    "Sets a warp at your current location",
    "/setwarp <name>",
    "zcore.setwarp"
) {
    stringArgument("warpName", StringType.SINGLE_WORD) {
        runs(permission) {
            val warpName: String by this
            doSetwarp(warpName)
        }
    }
}

private fun CommandContext<CommandSender>.doSetwarp(warpName: String) {
    val source = requirePlayer()

    if (!warpName.matches(Regex("[a-zA-Z0-9_-]+"))) {
        throw TranslatableException("command.setwarp.illegalName")
    }

    val location = source.location
    val existingWarp = ZCore.setWarp(warpName, location)
    if (existingWarp == null) {
        source.sendMessage(tl("command.setwarp.success",
            warpName, location.world.name, location.blockX, location.blockY, location.blockZ
        ))
    } else {
        throw TranslatableException("command.setwarp.alreadyExists", existingWarp.name)
    }
}