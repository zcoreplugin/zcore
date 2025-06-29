package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.ZCore
import me.zavdav.zcore.config.Config
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.player.core
import me.zavdav.zcore.punishment.MuteList
import me.zavdav.zcore.util.formatDuration
import me.zavdav.zcore.util.tl
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

internal val muteCommand = command(
    "mute",
    "Mutes a player.",
    "/mute <player> [<duration>] [<reason>]",
    "zcore.mute"
) {
    offlinePlayerArgument("target") {
        runs {
            val target: OfflinePlayer by this
            doMute(target, null, Config.defaultMuteReason)
        }
        durationArgument("duration") {
            runs {
                val target: OfflinePlayer by this
                val duration: Long by this
                doMute(target, duration, Config.defaultMuteReason)
            }
            textArgument("reason") {
                runs {
                    val target: OfflinePlayer by this
                    val duration: Long by this
                    val reason: String by this
                    doMute(target, duration, reason)
                }
            }
        }
        textArgument("reason") {
            runs {
                val target: OfflinePlayer by this
                val reason: String by this
                doMute(target, null, reason)
            }
        }
    }
}

private fun CommandContext<CommandSender>.doMute(target: OfflinePlayer, duration: Long?, reason: String) {
    val source = this.source
    val issuer = (source as? Player)?.core()?.data

    if (target.isMuted)
        throw TranslatableException("command.mute.alreadyMuted")

    MuteList.addMute(target, issuer, duration, reason)
    val player = ZCore.getPlayer(target.uuid)
    if (duration != null) {
        player?.sendMessage(tl("command.mute.temporary.message", formatDuration(duration), reason))
        source.sendMessage(tl("command.mute.temporary", target.name, formatDuration(duration), reason))
    } else {
        player?.sendMessage(tl("command.mute.permanent.message", reason))
        source.sendMessage(tl("command.mute.permanent", target.name, reason))
    }
}