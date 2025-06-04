package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.ZCore
import me.zavdav.zcore.util.tl
import org.bukkit.command.CommandSender

internal val delwarpCommand = command(
    "delwarp",
    "Deletes a warp.",
    "/delwarp <name>",
    "zcore.delwarp"
) {
    stringArgument("warpName", StringType.SINGLE_WORD) {
        runs(permission) {
            val warpName: String by this
            doDelwarp(warpName)
        }
    }
}

fun CommandContext<CommandSender>.doDelwarp(warpName: String) {
    val existingWarp = ZCore.deleteWarp(warpName) ?: throw TranslatableException("command.delwarp.doesNotExist")
    source.sendMessage(tl("command.delwarp.success", existingWarp.name))
}
