package me.zavdav.zcore.group

import me.zavdav.zcore.player.OfflinePlayer
import org.bukkit.Bukkit

internal interface GroupResolver {

    fun getMainGroup(player: OfflinePlayer): String

    fun getPrefix(player: OfflinePlayer): String

    fun getSuffix(player: OfflinePlayer): String

    companion object {

        private val resolver: GroupResolver = when {
            Bukkit.getPluginManager().isPluginEnabled("JPerms") -> JPermsGroupResolver()
            Bukkit.getPluginManager().isPluginEnabled("PermissionsEx") -> PermissionsExGroupResolver()
            else -> DefaultGroupResolver()
        }

        fun getMainGroup(player: OfflinePlayer): String = resolver.getMainGroup(player)

        fun getPrefix(player: OfflinePlayer): String = resolver.getPrefix(player)

        fun getSuffix(player: OfflinePlayer): String = resolver.getSuffix(player)

    }

}