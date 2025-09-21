package me.zavdav.zcore.group

import com.johnymuffin.jperms.beta.JohnyPerms
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.util.colored

internal class JPermsGroupResolver : GroupResolver {

    override fun getMainGroup(player: OfflinePlayer): String =
        JohnyPerms.getJPermsAPI().getUser(player.uuid)?.group?.name ?: ""

    override fun getPrefix(player: OfflinePlayer): String =
        JohnyPerms.getJPermsAPI().getUser(player.uuid)?.group?.prefix?.colored() ?: ""

    override fun getSuffix(player: OfflinePlayer): String =
        JohnyPerms.getJPermsAPI().getUser(player.uuid)?.group?.suffix?.colored() ?: ""

}