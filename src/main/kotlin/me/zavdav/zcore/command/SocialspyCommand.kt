package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.CorePlayer
import me.zavdav.zcore.player.core
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

internal val socialspyCommand = command(
    "socialspy",
    "Toggles a player's socialspy status.",
    "/socialspy [<player>]",
    "zcore.socialspy"
) {
    runs {
        val source = requirePlayer()
        doSocialspy(source)
    }
    playerArgument("target") {
        runs {
            val target: CorePlayer by this
            doSocialspy(target)
        }
    }
}

private fun CommandContext<CommandSender>.doSocialspy(target: CorePlayer) {
    val source = this.source
    val self = source is Player && source.core() == target
    if (!self) require("zcore.socialspy.other")

    val isSocialSpy = !target.data.isSocialSpy
    target.data.isSocialSpy = isSocialSpy

    if (isSocialSpy) {
        source.sendMessage(local("command.socialspy.enabled", target.name))
        if (!self) target.sendMessage(local("command.socialspy.enabled", target.name))
    } else {
        source.sendMessage(local("command.socialspy.disabled", target.name))
        if (!self) target.sendMessage(local("command.socialspy.disabled", target.name))
    }
}