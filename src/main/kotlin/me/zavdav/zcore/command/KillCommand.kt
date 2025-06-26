package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.CorePlayer
import me.zavdav.zcore.player.core
import me.zavdav.zcore.util.tl
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

internal val killCommand = command(
    "kill",
    "Kills a player.",
    "/kill [<player>]",
    "zcore.kill"
) {
    runs {
        val source = requirePlayer()
        doKill(source)
    }
    playerArgument("target") {
        runs {
            val target: CorePlayer by this
            doKill(target)
        }
    }
}

private fun CommandContext<CommandSender>.doKill(target: CorePlayer) {
    val source = this.source
    val self = source is Player && source.core() == target
    if (!self) require("zcore.kill.other")

    target.health = 0
    if (self)
        source.sendMessage(tl("command.kill"))
    else
        source.sendMessage(tl("command.kill.other", target.name))
}