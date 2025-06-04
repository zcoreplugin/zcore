package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.player.core
import me.zavdav.zcore.util.tl
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

internal val delhomeCommand = command(
    "delhome",
    arrayOf("dh"),
    "Deletes a home.",
    "/delhome <name>",
    "zcore.delhome"
) {
    stringArgument("homeName", StringType.SINGLE_WORD) {
        runs(permission) {
            val source = requirePlayer()
            val homeName: String by this
            doDelhome(source.data, homeName)
        }
    }
    offlinePlayerArgument("target") {
        stringArgument("homeName", StringType.SINGLE_WORD) {
            runs(permission) {
                val target: OfflinePlayer by this
                val homeName: String by this
                doDelhome(target, homeName)
            }
        }
    }
}

private fun CommandContext<CommandSender>.doDelhome(target: OfflinePlayer, homeName: String) {
    val source = this.source
    val self = source is Player && source.core().data.uuid == target.uuid
    if (!self) require("zcore.delhome.other")

    val existingHome = target.deleteHome(homeName) ?: throw TranslatableException("command.delhome.doesNotExist")
    if (self)
        source.sendMessage(tl("command.delhome.success", existingHome.name))
    else
        source.sendMessage(tl("command.delhome.success.other", target.name, existingHome.name))

}