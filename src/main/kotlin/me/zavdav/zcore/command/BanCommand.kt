package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.ZCore
import me.zavdav.zcore.config.Config
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.player.core
import me.zavdav.zcore.punishment.BanList
import me.zavdav.zcore.util.formatDuration
import me.zavdav.zcore.util.tl
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

internal val banCommand = command(
    "ban",
    "Bans a player.",
    "/ban <player> [<duration>] [<reason>]",
    "zcore.ban"
) {
    offlinePlayerArgument("target") {
        runs {
            val target: OfflinePlayer by this
            doBan(target, null, Config.defaultBanReason)
        }
        durationArgument("duration") {
            runs {
                val target: OfflinePlayer by this
                val duration: Long by this
                doBan(target, duration, Config.defaultBanReason)
            }
            textArgument("reason") {
                runs {
                    val target: OfflinePlayer by this
                    val duration: Long by this
                    val reason: String by this
                    doBan(target, duration, reason)
                }
            }
        }
        textArgument("reason") {
            runs {
                val target: OfflinePlayer by this
                val reason: String by this
                doBan(target, null, reason)
            }
        }
    }
}

private fun CommandContext<CommandSender>.doBan(target: OfflinePlayer, duration: Long?, reason: String) {
    val source = this.source
    val issuer = (source as? Player)?.core()?.data

    if (target.isBanned)
        throw TranslatableException("command.ban.alreadyBanned")

    BanList.addBan(target, issuer, duration, reason)
    val player = ZCore.getPlayer(target.uuid)
    if (duration != null) {
        player?.kickPlayer(tl("command.ban.temporary.message", formatDuration(duration), reason))
        source.sendMessage(tl("command.ban.temporary", target.name, formatDuration(duration), reason))
    } else {
        player?.kickPlayer(tl("command.ban.permanent.message", reason))
        source.sendMessage(tl("command.ban.permanent", target.name, reason))
    }
}