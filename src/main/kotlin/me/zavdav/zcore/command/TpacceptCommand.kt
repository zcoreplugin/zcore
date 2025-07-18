package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.player.CorePlayer
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender

internal val tpacceptCommand = command(
    "tpaccept",
    arrayOf("tpyes"),
    "Accepts a teleport request.",
    "/tpaccept [<player>]",
    "zcore.tpaccept"
) {
    runs {
        doTpaccept()
    }
    playerArgument("requester") {
        runs {
            val requester: CorePlayer by this
            doTpaccept(requester)
        }
    }
}

private fun CommandContext<CommandSender>.doTpaccept() {
    val source = requirePlayer()
    if (source.teleportRequests.isEmpty())
        throw TranslatableException("command.tpa.none")

    val request = source.teleportRequests.poll()
    val requester = request.source
    source.sendMessage(local("command.tpaccept", requester.name))
    requester.sendMessage(local("command.tpaccept.notify", source.name))

    if (request.here) {
        source.teleport(requester)
    } else {
        requester.teleport(source)
    }
}

private fun CommandContext<CommandSender>.doTpaccept(requester: CorePlayer) {
    val source = requirePlayer()
    val request = source.teleportRequests.firstOrNull { it.source == requester }
    if (request == null)
        throw TranslatableException("command.tpa.none.player", requester.name)

    source.teleportRequests.remove(request)
    source.sendMessage(local("command.tpaccept", requester.name))
    requester.sendMessage(local("command.tpaccept.notify", source.name))

    if (request.here) {
        source.teleport(requester)
    } else {
        requester.teleport(source)
    }
}