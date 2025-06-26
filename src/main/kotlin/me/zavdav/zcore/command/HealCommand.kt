package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.CorePlayer
import me.zavdav.zcore.player.core
import me.zavdav.zcore.util.tl
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

internal val healCommand = command(
    "heal",
    "Heals a player.",
    "/heal [<player>]",
    "zcore.heal"
) {
    runs {
        val source = requirePlayer()
        doHeal(source)
    }
    playerArgument("target") {
        runs {
            val target: CorePlayer by this
            doHeal(target)
        }
    }
}

private fun CommandContext<CommandSender>.doHeal(target: CorePlayer) {
    val source = this.source
    val self = source is Player && source.core() == target
    if (!self) require("zcore.heal.other")

    target.health = 20
    if (self)
        source.sendMessage(tl("command.heal"))
    else
        source.sendMessage(tl("command.heal.other", target.name))
}