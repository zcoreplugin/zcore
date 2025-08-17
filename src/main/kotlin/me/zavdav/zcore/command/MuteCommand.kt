package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.ZCore
import me.zavdav.zcore.config.ZCoreConfig
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.player.core
import me.zavdav.zcore.punishment.MuteList
import me.zavdav.zcore.util.formatDuration
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

internal val muteCommand = command(
    "mute",
    "Mutes a player",
    "zcore.mute"
) {
    offlinePlayerArgument("player") {
        runs {
            val player: OfflinePlayer by this
            doMute(player, null, ZCoreConfig.getString("command.mute.default-reason"))
        }
        durationArgument("duration") {
            runs {
                val player: OfflinePlayer by this
                val duration: Long by this
                doMute(player, duration, ZCoreConfig.getString("command.mute.default-reason"))
            }
            textArgument("reason") {
                runs {
                    val player: OfflinePlayer by this
                    val duration: Long by this
                    val reason: String by this
                    doMute(player, duration, reason)
                }
            }
        }
        textArgument("reason") {
            runs {
                val player: OfflinePlayer by this
                val reason: String by this
                doMute(player, null, reason)
            }
        }
    }
}

private fun CommandContext<CommandSender>.doMute(target: OfflinePlayer, duration: Long?, reason: String) {
    val source = this.source
    val issuer = (source as? Player)?.core()?.data

    MuteList.addMute(target, issuer, duration, reason)
    val player = ZCore.getPlayer(target.uuid)
    if (duration != null) {
        player?.sendMessage(local("command.mute.temporary.notify", formatDuration(duration), reason))
        source.sendMessage(local("command.mute.temporary", target.name, formatDuration(duration), reason))
    } else {
        player?.sendMessage(local("command.mute.permanent.notify", reason))
        source.sendMessage(local("command.mute.permanent", target.name, reason))
    }
}