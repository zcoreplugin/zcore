package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.CorePlayer
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender

internal val smiteCommand = command(
    "smite",
    arrayOf("lightning"),
    "Strikes lightning at the aimed block or at a player",
    "zcore.smite"
) {
    runs {
        doSmite()
    }
    playerArgument("player") {
        runs {
            val player: CorePlayer by this
            doSmite(player)
        }
    }
}

private fun CommandContext<CommandSender>.doSmite() {
    val source = requirePlayer()
    val location = source.getTargetBlock(hashSetOf(0, 8, 9), 120).location
    location.world.strikeLightning(location)
    source.sendMessage(local("command.smite"))
}

private fun CommandContext<CommandSender>.doSmite(target: CorePlayer) {
    val location = target.location
    location.world.strikeLightning(location)
    source.sendMessage(local("command.smite.player", target.name))
}