package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.CorePlayer
import me.zavdav.zcore.player.core
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

internal val socialspyCommand = command(
    "socialspy",
    "Changes a player's socialspy status",
    "zcore.socialspy"
) {
    literal("on") {
        runs {
            val source = requirePlayer()
            doSocialSpyOn(source)
        }
        playerArgument("player") {
            requiresPermission("zcore.socialspy.other")
            runs {
                val player: CorePlayer by this
                doSocialSpyOn(player)
            }
        }
    }
    literal("off") {
        runs {
            val source = requirePlayer()
            doSocialSpyOff(source)
        }
        playerArgument("player") {
            requiresPermission("zcore.socialspy.other")
            runs {
                val player: CorePlayer by this
                doSocialSpyOff(player)
            }
        }
    }
}

private fun CommandContext<CommandSender>.doSocialSpyOn(target: CorePlayer) {
    val source = this.source
    val self = source is Player && source.core() == target

    target.data.isSocialSpy = true
    source.sendMessage(local("command.socialspy.enabled", target.name))
    if (!self) target.sendMessage(local("command.socialspy.enabled", target.name))
}

private fun CommandContext<CommandSender>.doSocialSpyOff(target: CorePlayer) {
    val source = this.source
    val self = source is Player && source.core() == target

    target.data.isSocialSpy = false
    source.sendMessage(local("command.socialspy.disabled", target.name))
    if (!self) target.sendMessage(local("command.socialspy.disabled", target.name))
}