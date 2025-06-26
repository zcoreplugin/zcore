package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.CorePlayer
import me.zavdav.zcore.player.core
import me.zavdav.zcore.util.tl
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

internal val vanishCommand = command(
    "vanish",
    "Makes you invisible to other players.",
    "/vanish",
    "zcore.vanish"
) {
    runs {
        val source = requirePlayer()
        doVanish(source)
    }
    playerArgument("target") {
        runs {
            val target: CorePlayer by this
            doVanish(target)
        }
    }
}

private fun CommandContext<CommandSender>.doVanish(target: CorePlayer) {
    val source = this.source
    val self = source is Player && source.core() == target
    if (!self) require("zcore.vanish.other")

    val isVanished = !target.data.isVanished
    target.data.isVanished = isVanished

    if (isVanished) {
        if (self) {
            source.sendMessage(tl("command.vanish.enabled"))
        } else {
            source.sendMessage(tl("command.vanish.enabled.other", target.name))
            target.sendMessage(tl("command.vanish.enabled"))
        }
    } else {
        if (self) {
            source.sendMessage(tl("command.vanish.disabled"))
        } else {
            source.sendMessage(tl("command.vanish.disabled.other", target.name))
            target.sendMessage(tl("command.vanish.disabled"))
        }
    }
}