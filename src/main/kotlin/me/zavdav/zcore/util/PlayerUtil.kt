package me.zavdav.zcore.util

import me.zavdav.zcore.config.ZCoreConfig
import me.zavdav.zcore.player.CorePlayer
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.player.core
import org.bukkit.Bukkit
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
        .filter { it.core().data.isSocialSpy && it.hasPermission("zcore.socialspy") }
        .filter { exempt.none { uuid -> it.uniqueId == uuid } }
        .forEach { it.sendMessage(message) }
}

internal fun OfflinePlayer.checkIgnoring(target: CorePlayer): Boolean =
    ignores(target.data) && !target.hasPermission("zcore.ignore.bypass")

internal fun updateVanishStates() {
    for (pl in Bukkit.getOnlinePlayers()) {
        if (pl.core().data.isVanished && pl.hasPermission("zcore.vanish")) {
            Bukkit.getOnlinePlayers()
                .filter { !it.hasPermission("zcore.vanish.bypass") }
                .forEach { it.hidePlayer(pl) }
        } else {

            Bukkit.getOnlinePlayers().forEach { it.showPlayer(pl) }
        }
    }
}