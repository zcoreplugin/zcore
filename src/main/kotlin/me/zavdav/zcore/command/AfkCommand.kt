package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.CorePlayer
import me.zavdav.zcore.player.core
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

internal val afkCommand = command(
    "afk",
    "Toggles your AFK status.",
    "/afk",
    "zcore.afk"
) {
    runs {
        val source = requirePlayer()
        doAfk(source)
    }
    playerArgument("target") {
        runs {
            val target: CorePlayer by this
            doAfk(target)
        }
    }
}

private fun CommandContext<CommandSender>.doAfk(target: CorePlayer) {
    val source = this.source
    val self = source is Player && source.core() == target
    if (!self) require("zcore.afk.other")

    if (target.isAfk) target.updateActivity() else target.setInactive()
}