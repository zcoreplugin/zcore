package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.CorePlayer
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender

internal val killCommand = command(
    "kill",
    "Kills a player",
    "zcore.kill"
) {
    runs {
        val source = requirePlayer()
        doKill(source)
    }
    playerArgument("player") {
        requiresPermission("zcore.kill.other")
        runs {
            val player: CorePlayer by this
            doKill(player)
        }
    }
}

private fun CommandContext<CommandSender>.doKill(target: CorePlayer) {
    target.health = 0
    source.sendMessage(local("command.kill", target.name))
}