package me.zavdav.zcore.util

import me.zavdav.zcore.ZCore
import me.zavdav.zcore.config.ZCoreConfig
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.player.data
import me.zavdav.zcore.punishment.MuteList
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.UUID

internal fun computeNickname(player: OfflinePlayer): String {
    val nickname = player.nickname
    return if (nickname != null) {
        "§f${ZCoreConfig.getString("text.nick-prefix").colored()}$nickname§f"
    } else {
        player.name
    }
}

internal fun notifySocialSpy(message: String, vararg exempt: UUID) {
    Bukkit.getOnlinePlayers()
        .filter { it.data.isSocialSpy && it.hasPermission("zcore.socialspy") }
        .filter { exempt.none { uuid -> it.uniqueId == uuid } }
        .forEach { it.sendMessage(message) }
}

internal fun OfflinePlayer.checkIgnoring(target: Player): Boolean =
    ignores(target.data) && !target.hasPermission("zcore.ignore.bypass")

internal fun Player.checkMuted(): Boolean {
    val mute = MuteList.getActiveMute(data)
    if (mute != null) {
        val duration = mute.expiration?.let { it - System.currentTimeMillis() }
        if (duration != null) {
            sendMessage(local("command.mute.temporary.notify", ZCore.formatDuration(duration), mute.reason))
        } else {
            sendMessage(local("command.mute.permanent.notify", mute.reason))
        }
        return true
    }
    return false
}

internal fun updateVanishStates() {
    for (pl in Bukkit.getOnlinePlayers()) {
        if (pl.data.isVanished && pl.hasPermission("zcore.vanish")) {
            Bukkit.getOnlinePlayers()
                .filter { !it.hasPermission("zcore.vanish.bypass") }
                .forEach { it.hidePlayer(pl) }
        } else {
            Bukkit.getOnlinePlayers().forEach { it.showPlayer(pl) }
        }
    }
}