package me.zavdav.zcore

import com.mojang.brigadier.exceptions.CommandSyntaxException
import me.zavdav.zcore.command.CommandPermissionException
import me.zavdav.zcore.command.IllegalConsoleActionException
import me.zavdav.zcore.command.commandDispatcher
import me.zavdav.zcore.data.Accounts
import me.zavdav.zcore.data.BankAccountUsers
import me.zavdav.zcore.data.BankAccounts
import me.zavdav.zcore.data.Bans
import me.zavdav.zcore.data.Homes
import me.zavdav.zcore.data.Ignores
import me.zavdav.zcore.data.IpBanUuids
import me.zavdav.zcore.data.IpBans
import me.zavdav.zcore.data.KitItems
import me.zavdav.zcore.data.Kits
import me.zavdav.zcore.data.Locations
import me.zavdav.zcore.data.Mails
import me.zavdav.zcore.data.Mutes
import me.zavdav.zcore.data.OfflineUsers
import me.zavdav.zcore.data.Punishments
import me.zavdav.zcore.data.Warps
import me.zavdav.zcore.data.WorldSpawns
import me.zavdav.zcore.economy.BankAccount
import me.zavdav.zcore.kit.Kit
import me.zavdav.zcore.location.Warp
import me.zavdav.zcore.location.WorldSpawn
import me.zavdav.zcore.user.OfflineUser
import me.zavdav.zcore.util.tl
import me.zavdav.zcore.version.ZCoreVersion
import org.bukkit.World
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal
import java.util.UUID

/** The main class of the ZCore plugin. */
class ZCore : JavaPlugin() {

    override fun onEnable() {
        INSTANCE = this
        Database.connect("jdbc:h2:${dataFolder.absolutePath}/db/zcore", "org.h2.Driver")

        transaction {
            SchemaUtils.create(
                OfflineUsers,
                Accounts,
                BankAccounts,
                BankAccountUsers,
                Punishments,
                Mutes,
                Bans,
                IpBans,
                IpBanUuids,
                Locations,
                WorldSpawns,
                Warps,
                Homes,
                Kits,
                KitItems,
                Mails,
                Ignores
            )
        }
    }

    override fun onDisable() { }

    override fun onCommand(
        sender: CommandSender, command: Command,
        label: String, args: Array<String>
    ): Boolean {
        try {
            commandDispatcher.execute("${command.name} ${args.joinToString(" ")}", sender)
        } catch (_: CommandSyntaxException) {
            sender.sendMessage(tl("command.syntaxError"))
        } catch (_: CommandPermissionException) {
            sender.sendMessage(tl("command.noPermission"))
        } catch (_: IllegalConsoleActionException) {
            sender.sendMessage(tl("command.playerRequired"))
        } catch (_: Throwable) {
            sender.sendMessage(tl("command.genericError"))
        }

        return true
    }

    /** Represents the ZCore API. */
    companion object Api {

        /** The current instance of ZCore. */
        @JvmStatic
        lateinit var INSTANCE: ZCore
            private set

        /** An intermediary user for actions that aren't directly performed by a user. */
        @JvmStatic
        val SYSTEM_USER: OfflineUser
            get() = TODO("Not yet implemented")

        /** The current version of ZCore. */
        @JvmStatic
        val version: ZCoreVersion by lazy { ZCoreVersion.CURRENT }

        /** All users that have played on the server. */
        @JvmStatic
        val users: Iterable<OfflineUser> get() = OfflineUser.all()

        /** The spawn points of all worlds. */
        @JvmStatic
        val worldSpawns: Iterable<WorldSpawn> get() = WorldSpawn.all()

        /** All existing warps. */
        @JvmStatic
        val warps: Iterable<Warp> get() = Warp.all()

        /** All existing kits. */
        @JvmStatic
        val kits: Iterable<Kit> get() = Kit.all()

        /** All existing bank accounts. */
        @JvmStatic
        val bankAccounts: Iterable<BankAccount> get() = BankAccount.all()

        /** Gets an offline user by their [uuid], or `null` if no such user exists. */
        @JvmStatic
        fun getOfflineUser(uuid: UUID): OfflineUser? =
            OfflineUser.findById(uuid)

        /** Gets an offline user by their [name], or `null` if no such user exists. */
        @JvmStatic
        fun getOfflineUser(name: String): OfflineUser? =
            OfflineUser.find { OfflineUsers.name.lowerCase() eq name.lowercase() }.firstOrNull()

        /** Gets a bank account by its [uuid], or `null` if no such bank account exists. */
        @JvmStatic
        fun getBankAccount(uuid: UUID): BankAccount? =
            BankAccount.findById(uuid)

        /** Gets a bank account by its [name], or `null` if no such bank account exists. */
        @JvmStatic
        fun getBankAccount(name: String): BankAccount? =
            BankAccount.find { BankAccounts.name.lowerCase() eq name.lowercase() }.firstOrNull()

        /** Gets the spawn point of a [world], or `null` if this world does not exist. */
        @JvmStatic
        fun getWorldSpawn(world: World): WorldSpawn? =
            WorldSpawn.find { Locations.world.lowerCase() eq world.name.lowercase() }.firstOrNull()

        /** Sets the spawn point of a [world] to a [location]. */
        @JvmStatic
        fun setWorldSpawn(world: World, location: org.bukkit.Location) {
            world.setSpawnLocation(location.x.toInt(), location.y.toInt(), location.z.toInt())
            WorldSpawns.deleteWhere { Locations.world.lowerCase() eq world.name.lowercase() }
            WorldSpawn.new(
                world.name,
                location.x,
                location.y,
                location.z,
                location.pitch,
                location.yaw
            )
        }

        /** Gets a warp by its [name], or `null` if no such warp exists. */
        @JvmStatic
        fun getWarp(name: String): Warp? =
            Warp.find { Warps.name.lowerCase() eq name.lowercase() }.firstOrNull()

        /**
         * Sets a new warp with a [name] and a [location].
         * Returns `null` on success, or the warp with this name if it already exists.
         */
        @JvmStatic
        fun setWarp(name: String, location: org.bukkit.Location): Warp? {
            val warp = getWarp(name)
            if (warp == null) {
                Warp.new(
                    name,
                    location.world.name,
                    location.x,
                    location.y,
                    location.z,
                    location.pitch,
                    location.yaw
                )
            }
            return warp
        }

        /**
         * Deletes the warp with the specified [name].
         * Returns the warp that was deleted, or `null` if no warp with this name exists.
         */
        @JvmStatic
        fun deleteWarp(name: String): Warp? {
            val warp = getWarp(name)
            warp?.delete()
            return warp
        }

        /** Gets a kit by its [name], or `null` if no such kit exists. */
        @JvmStatic
        fun getKit(name: String): Kit? =
            Kit.find { Kits.name.lowerCase() eq name.lowercase() }.firstOrNull()

        /**
         * Sets a new kit with a [name] that users can equip.
         * Returns `null` on success, or the kit with this name if it already exists.
         */
        @JvmStatic
        fun setKit(
            name: String,
            items: Map<Int, ItemStack>,
            cost: BigDecimal = BigDecimal.ZERO,
            cooldown: Long = 0
        ): Kit? {
            val kit = getKit(name)
            if (kit == null) {
                Kit.new(name, items, cost, cooldown)
            }
            return kit
        }

        /**
         * Deletes the kit with the specified [name].
         * Returns the kit that was deleted, or `null` if no kit with this name exists.
         */
        @JvmStatic
        fun deleteKit(name: String): Kit? {
            val kit = getKit(name)
            kit?.delete()
            return kit
        }

    }

}