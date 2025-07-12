package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.CorePlayer
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender

internal val tphereCommand = command(
    "tphere",
    "Teleports a player to you.",
    "/tphere <player>",
    "zcore.tphere"
) {
    playerArgument("target") {
        runs {
            val target: CorePlayer by this
            doTphere(target)
        }
    }
}

private fun CommandContext<CommandSender>.doTphere(target: CorePlayer) {
    val source = requirePlayer()
    target.teleport(source)
    source.sendMessage(local("command.tp.player", target.name, source.name))
}