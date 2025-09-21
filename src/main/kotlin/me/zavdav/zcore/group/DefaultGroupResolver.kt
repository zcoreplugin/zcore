package me.zavdav.zcore.group

import me.zavdav.zcore.player.OfflinePlayer

class DefaultGroupResolver : GroupResolver {

    override fun getMainGroup(player: OfflinePlayer): String = ""

    override fun getPrefix(player: OfflinePlayer): String = ""

    override fun getSuffix(player: OfflinePlayer): String = ""

}