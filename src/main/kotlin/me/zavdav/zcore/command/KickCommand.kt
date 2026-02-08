package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.config.ZCoreConfig
import me.zavdav.zcore.player.kick
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

internal val kickCommand = command(
    "kick",
    "Kicks a player",
    "zcore.kick"
) {
    playerArgument("player") {
        runs {
            val player: Player by this
            doKick(player, ZCoreConfig.getString("command.kick.default-reason"))
        }
        textArgument("reason") {
            runs {
                val player: Player by this
                val reason: String by this
                doKick(player, reason)
            }
        }
    }
}

private fun CommandContext<CommandSender>.doKick(target: Player, reason: String) {
    target.kick(local("command.kick.message", reason))
    source.sendMessage(local("command.kick", target.name, reason))
}