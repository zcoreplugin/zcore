package me.zavdav.zcore.command

import com.mojang.brigadier.exceptions.CommandSyntaxException
import me.zavdav.zcore.config.ZCoreConfig
import me.zavdav.zcore.util.getField
import me.zavdav.zcore.util.local
import me.zavdav.zcore.util.syncDelayedTask
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandMap
import org.bukkit.command.CommandSender

internal object CommandDispatcher : com.mojang.brigadier.CommandDispatcher<CommandSender>() {

    private val commands = mutableListOf(
        afkCommand,
        balanceCommand,
        baltopCommand,
        banCommand,
        banipCommand,
        bankCommand,
        bansCommand,
        broadcastCommand,
        clearCommand,
        delhomeCommand,
        delptCommand,
        delwarpCommand,
        ecoCommand,
        giveCommand,
        godCommand,
        healCommand,
        helpCommand,
        homeCommand,
        homesCommand,
        ignoreCommand,
        ignoredCommand,
        invseeCommand,
        itemCommand,
        kickCommand,
        kickallCommand,
        killCommand,
        leaderboardCommand,
        listCommand,
        mailCommand,
        meCommand,
        movehomeCommand,
        msgCommand,
        muteCommand,
        mutesCommand,
        nickCommand,
        payCommand,
        ptsCommand,
        rCommand,
        realnameCommand,
        renamehomeCommand,
        rulesCommand,
        seenCommand,
        sethomeCommand,
        setptCommand,
        setspawnCommand,
        setwarpCommand,
        smiteCommand,
        socialspyCommand,
        spawnCommand,
        spawnerCommand,
        statsCommand,
        summonCommand,
        timeCommand,
        tpCommand,
        tpaCommand,
        tpacceptCommand,
        tpahereCommand,
        tpdenyCommand,
        tphereCommand,
        unbanCommand,
        unbanipCommand,
        ungodCommand,
        unignoreCommand,
        unmuteCommand,
        unnickCommand,
        unvanishCommand,
        vanishCommand,
        warpCommand,
        warpsCommand,
        weatherCommand,
        zcoreCommand
    )

    private val bukkitCommandMap = getField<CommandMap>(Bukkit.getPluginManager(), "commandMap")
    private val bukkitKnownCommands = getField<MutableMap<String, Command>>(bukkitCommandMap, "knownCommands")

    init {
        commands.removeAll { it.name in ZCoreConfig.getStringList("command.disabled") }
        commands.forEach { root.addChild(it.node) }
    }

    internal fun registerAll() {
        val overridden = commands.filter { it.name in ZCoreConfig.getStringList("command.overridden") }
        commands.removeAll(overridden)
        bukkitCommandMap.registerAll("zcore", commands.toList())

        syncDelayedTask(1) {
            for (command in overridden) {
                val matches = bukkitKnownCommands.filter { it.value.name.equals(command.name, true) }
                matches.forEach {
                    bukkitKnownCommands.remove(it.key)
                    it.value.unregister(bukkitCommandMap)
                }

                commands.add(command)
                bukkitCommandMap.register("zcore", command)
                matches.forEach { bukkitCommandMap.register(it.key, it.value) }
            }
        }
    }

    internal fun unregisterAll() {
        bukkitKnownCommands.entries.removeIf { it.value is CoreCommand }
    }

    internal fun getCommands(): List<Command> =
        bukkitKnownCommands.values
            .distinct()
            .sortedWith { c1, c2 -> c1.name.compareTo(c2.name, true) }

    internal fun getCommand(name: String): Command? = bukkitKnownCommands[name.lowercase()]

    internal fun execute(source: CommandSender, command: CoreCommand, args: Array<String>) {
        try {
            val parsedArgs = if (args.isEmpty()) "" else " " + args.joinToString(" ").trim()
            execute(command.name + parsedArgs, source)
        } catch (e: CommandSyntaxException) {
            when (e.type) {
                is NameNoMatchesExceptionType,
                is NameMultipleMatchesExceptionType,
                is UnknownPlayerExceptionType,
                is UnknownBankExceptionType,
                is UnknownCreatureExceptionType,
                is UnknownMaterialExceptionType -> source.sendMessage(e.rawMessage.string)
                else -> {
                    source.sendMessage(local("command.correctSyntax"))
                    val syntaxList = getAllUsage(command.node, source, true).map { "/${command.name} $it" }
                    syntaxList.forEach { source.sendMessage(local("command.syntaxLine", it)) }
                    source.sendMessage(local("command.commandHelp", command.name))
                }
            }
        } catch (e: TranslatableException) {
            source.sendMessage(local(e.key, *e.args))
        } catch (e: Throwable) {
            e.printStackTrace()
            source.sendMessage(local("command.genericError"))
        }
    }

}