package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.command.event.TeleportDenyEvent
import me.zavdav.zcore.player.teleportRequests
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

internal val tpdenyCommand = command(
    "tpdeny",
    arrayOf("tpno"),
    "Denies a teleport request",
    "zcore.tpdeny"
) {
    runs {
        doTpDeny()
    }
    playerArgument("player") {
        runs {
            val player: Player by this
            doTpDeny(player)
        }
    }
}

private fun CommandContext<CommandSender>.doTpDeny() {
    val source = requirePlayer()
    val request = source.teleportRequests.peek()
    if (request == null || request.ignore)
        throw TranslatableException("command.tpa.none")

    val requester = request.source
    if (!TeleportDenyEvent(source, request).call()) return
    source.teleportRequests.poll()
    source.sendMessage(local("command.tpdeny", requester.name))
    requester.sendMessage(local("command.tpdeny.notify", source.name))
}

private fun CommandContext<CommandSender>.doTpDeny(requester: Player) {
    val source = requirePlayer()
    val request = source.teleportRequests.firstOrNull { it.source == requester }
    if (request == null || request.ignore)
        throw TranslatableException("command.tpa.none.player", requester.name)

    if (!TeleportDenyEvent(source, request).call()) return
    source.teleportRequests.remove(request)
    source.sendMessage(local("command.tpdeny", requester.name))
    requester.sendMessage(local("command.tpdeny.notify", source.name))
}