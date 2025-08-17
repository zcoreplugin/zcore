package me.zavdav.zcore.command

import com.mojang.brigadier.context.CommandContext
import me.zavdav.zcore.util.local
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

internal val timeCommand = command(
    "time",
    "Changes the time",
    "zcore.time"
) {
    literal("set") {
        intArgument("ticks") {
            runs {
                val ticks: Int by this
                doTimeSet(ticks)
            }
        }
        literal("sunrise") {
            runs {
                doTimeSet(23000)
            }
        }
        literal("day") {
            runs {
                doTimeSet(1000)
            }
        }
        literal("noon") {
            runs {
                doTimeSet(6000)
            }
        }
        literal("afternoon") {
            runs {
                doTimeSet(9000)
            }
        }
        literal("sunset") {
            runs {
                doTimeSet(12000)
            }
        }
        literal("night") {
            runs {
                doTimeSet(14000)
            }
        }
        literal("midnight") {
            runs {
                doTimeSet(18000)
            }
        }
    }
    literal("add") {
        intArgument("ticks") {
            runs {
                val ticks: Int by this
                doTimeAdd(ticks)
            }
        }
    }
}

private fun CommandContext<CommandSender>.doTimeSet(ticks: Int) {
    val world = Bukkit.getWorlds()[0]
    val finalTicks = ticks.coerceAtLeast(0) % 24000L
    world.time = finalTicks
    source.sendMessage(local("command.time.set", finalTicks))
}

private fun CommandContext<CommandSender>.doTimeAdd(ticks: Int) {
    val world = Bukkit.getWorlds()[0]
    val finalTicks = ticks.coerceAtLeast(0) % 24000L
    world.time += finalTicks
    source.sendMessage(local("command.time.add", finalTicks))
}