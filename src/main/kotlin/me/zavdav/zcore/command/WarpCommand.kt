package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.ZCore
import me.zavdav.zcore.util.normalizedDirection
import me.zavdav.zcore.util.tl
import org.bukkit.command.CommandSender

internal val warpCommand = command(
    "warp",
    "Teleports you to a warp.",
    "/warp <name>",
    "zcore.warp"
) {
    stringArgument("warpName", StringType.SINGLE_WORD) {
        runs(permission) {
            val warpName: String by this
            doWarp(warpName)
        }
    }
}

private fun CommandContext<CommandSender>.doWarp(warpName: String) {
    val source = requirePlayer()
    val warp = ZCore.getWarp(warpName) ?: throw TranslatableException("command.warp.doesNotExist")

    val location = warp.toBukkitLocation().normalizedDirection()
    if (!source.safelyTeleport(location)) throw TranslatableException("command.warp.unsafeLocation")
    source.sendMessage(tl("command.warp.success", warp.name))
}