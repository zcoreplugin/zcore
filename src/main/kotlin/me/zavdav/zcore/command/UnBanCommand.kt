package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.punishment.BanList
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender

internal val unbanCommand = command(
    "unban",
    arrayOf("pardon"),
    "Unbans a player",
    "zcore.unban"
) {
    offlinePlayerArgument("player") {
        runs {
            val player: OfflinePlayer by this
            doUnBan(player)
        }
    }
}

private fun CommandContext<CommandSender>.doUnBan(target: OfflinePlayer) {
    if (BanList.pardonBan(target)) {
        source.sendMessage(local("command.unban", target.name))
    } else {
        throw TranslatableException("command.unban.notBanned", target.name)
    }
}