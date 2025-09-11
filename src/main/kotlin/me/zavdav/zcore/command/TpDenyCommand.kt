package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.CorePlayer
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender

internal val tpdenyCommand = command(
    "tpdeny",
    arrayOf("tpno"),
    "Denies a teleport request",
    "zcore.tpdeny"
) {
    runs {
        doTpdeny()
    }
    playerArgument("player") {
        runs {
            val player: CorePlayer by this
            doTpdeny(player)
        }
    }
}

private fun CommandContext<CommandSender>.doTpdeny() {
    val source = requirePlayer()
    if (source.teleportRequests.isEmpty())
        throw TranslatableException("command.tpa.none")

    val request = source.teleportRequests.poll()
    val requester = request.source
    source.sendMessage(local("command.tpdeny", requester.name))
    requester.sendMessage(local("command.tpdeny.notify", source.name))
}

private fun CommandContext<CommandSender>.doTpdeny(requester: CorePlayer) {
    val source = requirePlayer()
    val request = source.teleportRequests.firstOrNull { it.source == requester }
    if (request == null)
        throw TranslatableException("command.tpa.none.player", requester.name)

    source.teleportRequests.remove(request)
    source.sendMessage(local("command.tpdeny", requester.name))
    requester.sendMessage(local("command.tpdeny.notify", source.name))
}