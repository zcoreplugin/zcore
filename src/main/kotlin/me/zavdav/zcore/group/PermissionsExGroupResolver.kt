package me.zavdav.zcore.group

import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.util.colored
import ru.tehkode.permissions.bukkit.PermissionsEx

internal class PermissionsExGroupResolver : GroupResolver {

    override fun getMainGroup(player: OfflinePlayer): String =
        PermissionsEx.getPermissionManager().getUser(player.name)?.groupsNames?.firstOrNull() ?: ""

    override fun getPrefix(player: OfflinePlayer): String =
        PermissionsEx.getPermissionManager().getUser(player.name)?.prefix?.colored() ?: ""

    override fun getSuffix(player: OfflinePlayer): String =
        PermissionsEx.getPermissionManager().getUser(player.name)?.suffix?.colored() ?: ""

}