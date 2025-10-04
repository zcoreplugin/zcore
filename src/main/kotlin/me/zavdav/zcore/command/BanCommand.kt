package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.ZCore
import me.zavdav.zcore.command.event.PlayerBanEvent
import me.zavdav.zcore.config.ZCoreConfig
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.player.core
import me.zavdav.zcore.punishment.BanList
import me.zavdav.zcore.util.local
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

internal val banCommand = command(
    "ban",
    "Bans a player",
    "zcore.ban"
) {
    offlinePlayerArgument("player") {
        runs {
            val player: OfflinePlayer by this
            doBan(player, null, ZCoreConfig.getString("command.ban.default-reason"))
        }
        durationArgument("duration") {
            runs {
                val player: OfflinePlayer by this
                val duration: Long by this
                doBan(player, duration, ZCoreConfig.getString("command.ban.default-reason"))
            }
            textArgument("reason") {
                runs {
                    val player: OfflinePlayer by this
                    val duration: Long by this
                    val reason: String by this
                    doBan(player, duration, reason)
                }
            }
        }
        textArgument("reason") {
            runs {
                val player: OfflinePlayer by this
                val reason: String by this
                doBan(player, null, reason)
            }
        }
    }
}

private fun CommandContext<CommandSender>.doBan(target: OfflinePlayer, duration: Long?, reason: String) {
    val source = this.source
    val issuer = (source as? Player)?.core()?.data

    if (Bukkit.getOfflinePlayer(target.name).isOp)
        throw TranslatableException("command.ban.exempt", target.name)
    if (!PlayerBanEvent(source, target, duration, reason).call()) return

    BanList.addBan(target, issuer, duration, reason)
    val player = ZCore.getPlayer(target.uuid)
    if (duration != null) {
        player?.kickPlayer(local("command.ban.temporary.notify", ZCore.formatDuration(duration), reason))
        source.sendMessage(local("command.ban.temporary", target.name, ZCore.formatDuration(duration), reason))
    } else {
        player?.kickPlayer(local("command.ban.permanent.notify", reason))
        source.sendMessage(local("command.ban.permanent", target.name, reason))
    }
}