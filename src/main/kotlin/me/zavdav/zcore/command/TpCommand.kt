package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.config.ZCoreConfig
import me.zavdav.zcore.player.CorePlayer
import me.zavdav.zcore.util.local
import org.bukkit.Location
import org.bukkit.command.CommandSender
import java.math.BigDecimal
import java.math.RoundingMode

internal val tpCommand = command(
    "tp",
    "Teleports a player to a location.",
    "/tp [<player>] (<player>|<x> <y> <z>)",
    "zcore.tp"
) {
    playerArgument("target") {
        runs {
            val source = requirePlayer()
            val target: CorePlayer by this
            doTpPlayer(source, target)
        }
        playerArgument("target2") {
            runs {
                val target: CorePlayer by this
                val target2: CorePlayer by this
                doTpPlayer(target, target2)
            }
        }
        bigDecimalArgument("x") {
            bigDecimalArgument("y") {
                bigDecimalArgument("z") {
                    runs {
                        val target: CorePlayer by this
                        val x: BigDecimal by this
                        val y: BigDecimal by this
                        val z: BigDecimal by this
                        doTpLocation(target, x, y, z)
                    }
                }
            }
        }
    }
    bigDecimalArgument("x") {
        bigDecimalArgument("y") {
            bigDecimalArgument("z") {
                runs {
                    val source = requirePlayer()
                    val x: BigDecimal by this
                    val y: BigDecimal by this
                    val z: BigDecimal by this
                    doTpLocation(source, x, y, z)
                }
            }
        }
    }
}

private fun CommandContext<CommandSender>.doTpPlayer(player: CorePlayer, target: CorePlayer) {
    player.teleport(target)
    source.sendMessage(local("command.tp.player", player.name, target.name))
}

private fun CommandContext<CommandSender>.doTpLocation(
    player: CorePlayer, x: BigDecimal, y: BigDecimal, z: BigDecimal
) {
    val maxRadius = ZCoreConfig.getInt("command.tp.max-radius").toBigDecimal()
    var finalX = x.coerceIn(-maxRadius, maxRadius)
    var finalY = y
    var finalZ = z.coerceIn(-maxRadius, maxRadius)

    if (finalX.scale() == 0)
        finalX += BigDecimal("0.5")
    if (finalZ.scale() == 0)
        finalZ += BigDecimal("0.5")

    finalX = finalX.setScale(6, RoundingMode.DOWN)
    finalY = finalY.setScale(6, RoundingMode.DOWN)
    finalZ = finalZ.setScale(6, RoundingMode.DOWN)

    val location = Location(
        player.world,
        finalX.toDouble(), finalY.toDouble(), finalZ.toDouble(),
        player.location.yaw, player.location.pitch
    )

    player.world.loadChunk(location.blockX, location.blockZ)
    player.teleport(location)
    source.sendMessage(local("command.tp.location", player.name, finalX, finalY, finalZ))
}