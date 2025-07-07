package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.punishment.BanList
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender

internal val unbanCommand = command(
    "unban",
    "Unbans a player.",
    "/unban <player>",
    "zcore.unban"
) {
    offlinePlayerArgument("target") {
        runs {
            val target: OfflinePlayer by this
            doUnban(target)
        }
    }
}

private fun CommandContext<CommandSender>.doUnban(target: OfflinePlayer) {
    if (BanList.pardonBan(target)) {
        source.sendMessage(local("command.unban", target.name))
    } else {
        throw TranslatableException("command.unban.notBanned", target.name)
    }
}