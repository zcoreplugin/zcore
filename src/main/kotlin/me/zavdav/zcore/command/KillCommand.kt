package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

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
            val player: Player by this
            doKill(player)
        }
    }
}

private fun CommandContext<CommandSender>.doKill(target: Player) {
    target.health = 0
    source.sendMessage(local("command.kill", target.name))
}