package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.config.Config
import me.zavdav.zcore.player.CorePlayer
import me.zavdav.zcore.util.tl
import org.bukkit.command.CommandSender

internal val kickCommand = command(
    "kick",
    "Kicks a player.",
    "/kick <player> [<reason>]",
    "zcore.kick"
) {
    playerArgument("target") {
        runs {
            val target: CorePlayer by this
            doKick(target, Config.defaultKickReason)
        }
        textArgument("reason") {
            runs {
                val target: CorePlayer by this
                val reason: String by this
                doKick(target, reason)
            }
        }
    }
}

private fun CommandContext<CommandSender>.doKick(target: CorePlayer, reason: String) {
    target.kickPlayer(tl("command.kick.message", reason))
    source.sendMessage(tl("command.kick", target.name, reason))
}