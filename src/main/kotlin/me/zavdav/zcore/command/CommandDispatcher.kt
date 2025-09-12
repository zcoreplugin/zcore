package me.zavdav.zcore.command

import com.mojang.brigadier.exceptions.CommandSyntaxException
import me.zavdav.zcore.util.getField
import me.zavdav.zcore.util.local
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandMap
import org.bukkit.command.CommandSender

internal object CommandDispatcher : com.mojang.brigadier.CommandDispatcher<CommandSender>() {

    private val commands = listOf(
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
        motdCommand,
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
        commands.forEach { root.addChild(it.node) }
    }

    internal fun registerAll() {
        bukkitCommandMap.registerAll("zcore", commands)
    }

    internal fun unregisterAll() {
        bukkitKnownCommands.entries.removeIf { it.value is CoreCommand }
    }

    internal fun getCommands(): List<Command> =
        bukkitKnownCommands.values
            .distinct()
            .sortedWith { c1, c2 -> c1.name.compareTo(c2.name, true) }

    internal fun getCommand(name: String): Command? = bukkitKnownCommands[name.lowercase()]

    override fun execute(input: String, source: CommandSender): Int {
        try {
            return super.execute(input, source)
        } catch (e: CommandSyntaxException) {
            when (e.type) {
                is NameNoMatchesExceptionType,
                is NameMultipleMatchesExceptionType,
                is UnknownPlayerExceptionType,
                is UnknownCreatureExceptionType,
                is UnknownMaterialExceptionType -> source.sendMessage(e.rawMessage.string)
                else -> source.sendMessage(local("command.syntaxError"))
            }
        } catch (e: TranslatableException) {
            source.sendMessage(local(e.key, *e.args))
        } catch (e: Throwable) {
            e.printStackTrace()
            source.sendMessage(local("command.genericError"))
        }

        return 0
    }

}