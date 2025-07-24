package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.ZCore
import me.zavdav.zcore.util.local
import org.bukkit.command.CommandSender

internal val zcoreCommand = command(
    "zcore",
    "Shows information about ZCore.",
    "/zcore",
    "zcore.zcore"
) {
    runs {
        doZCore()
    }
    literal("reload") {
        runs {
            doZCoreReload()
        }
    }
}

private fun CommandContext<CommandSender>.doZCore() {
    source.sendMessage(local("command.zcore.version", ZCore.version))
    source.sendMessage(local("command.zcore.description"))
    source.sendMessage(local("command.zcore.author"))
    source.sendMessage(local("command.zcore.repository"))
}

private fun CommandContext<CommandSender>.doZCoreReload() {
    require("zcore.zcore.reload")
    source.sendMessage(local("command.zcore.reload.start"))
    ZCore.INSTANCE.onDisable()
    ZCore.INSTANCE.onEnable()
    source.sendMessage(local("command.zcore.reload.finish"))
}