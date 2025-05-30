package me.zavdav.zcore

import me.zavdav.zcore.command.CommandDispatcher
import me.zavdav.zcore.command.TranslatableException
import me.zavdav.zcore.command.motdCommand
import me.zavdav.zcore.config.Config
import me.zavdav.zcore.data.BanEntries
import me.zavdav.zcore.data.BankAccounts
import me.zavdav.zcore.data.BankMembers
import me.zavdav.zcore.data.Homes
import me.zavdav.zcore.data.Ignores
import me.zavdav.zcore.data.IpBanEntries
import me.zavdav.zcore.data.KitItems
import me.zavdav.zcore.data.Kits
import me.zavdav.zcore.data.Mails
import me.zavdav.zcore.data.MuteEntries
import me.zavdav.zcore.data.OfflinePlayers
import me.zavdav.zcore.data.PersonalAccounts
import me.zavdav.zcore.data.Warps
import me.zavdav.zcore.economy.BankAccount
import me.zavdav.zcore.kit.Kit
import me.zavdav.zcore.kit.KitItem
import me.zavdav.zcore.location.Warp
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.util.tl
import me.zavdav.zcore.version.ZCoreVersion
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.math.BigDecimal
import java.sql.Connection
import java.util.UUID

/** The main class of the ZCore plugin. */
class ZCore : JavaPlugin() {

    private lateinit var transaction: Transaction

    override fun onEnable() {
        INSTANCE = this
        Config.load()
        Database.connect("jdbc:h2:${dataFolder.absolutePath}/db/zcore", "org.h2.Driver")
        transaction = TransactionManager.currentOrNew(Connection.TRANSACTION_REPEATABLE_READ)

        SchemaUtils.create(
            OfflinePlayers,
            PersonalAccounts,
            BankAccounts,
            BankMembers,
            MuteEntries,
            BanEntries,
            IpBanEntries,
            Homes,
            Warps,
            Kits,
            KitItems,
            Mails,
            Ignores
        )

        val commands = mutableListOf(
            motdCommand
        )

        commands.forEach { it.register() }
    }

    override fun onDisable() {
        transaction.commit()
        transaction.close()
    }

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): Boolean {
        try {
            val parsedArgs = if (args.isEmpty()) "" else " " + args.joinToString(" ").trim()
            CommandDispatcher.execute(command.name + parsedArgs, sender)
        } catch (e: TranslatableException) {
            sender.sendMessage(tl(e.key, *e.args))
        }

        return true
    }

    /** Represents the ZCore API. */
    companion object Api {

        /** The current instance of ZCore. */
        @JvmStatic
        lateinit var INSTANCE: ZCore
            private set

        /** An intermediary player for actions that aren't directly performed by a player. */
        @JvmStatic
        val SYSTEM_PLAYER: OfflinePlayer
            get() = TODO("Not yet implemented")

        /** The current version of ZCore. */
        @JvmStatic
        val version: ZCoreVersion by lazy { ZCoreVersion.CURRENT }

        /** All players that have played before. */
        @JvmStatic
        val players: Iterable<OfflinePlayer> get() = OfflinePlayer.all()

        /** All existing warps. */
        @JvmStatic
        val warps: Iterable<Warp> get() = Warp.all()

        /** All existing kits. */
        @JvmStatic
        val kits: Iterable<Kit> get() = Kit.all()

        /** All existing bank accounts. */
        @JvmStatic
        val bankAccounts: Iterable<BankAccount> get() = BankAccount.all()

        /** Gets an offline player by their [uuid], or `null` if no such player exists. */
        @JvmStatic
        fun getOfflinePlayer(uuid: UUID): OfflinePlayer? =
            OfflinePlayer.findById(uuid)

        /** Gets an offline player by their [name], or `null` if no such player exists. */
        @JvmStatic
        fun getOfflinePlayer(name: String): OfflinePlayer? =
            OfflinePlayer.find { OfflinePlayers.name.lowerCase() eq name.lowercase() }.firstOrNull()

        /** Gets a bank account by its [uuid], or `null` if no such bank account exists. */
        @JvmStatic
        fun getBankAccount(uuid: UUID): BankAccount? =
            BankAccount.findById(uuid)

        /** Gets a bank account by its [name], or `null` if no such bank account exists. */
        @JvmStatic
        fun getBankAccount(name: String): BankAccount? =
            BankAccount.find { BankAccounts.name.lowerCase() eq name.lowercase() }.firstOrNull()

        /** Gets a warp by its [name], or `null` if no such warp exists. */
        @JvmStatic
        fun getWarp(name: String): Warp? =
            Warp.find { Warps.name.lowerCase() eq name.lowercase() }.firstOrNull()

        /**
         * Sets a new warp with a [name] and a [location].
         * Returns `null` on success, or the warp with this name if it already exists.
         */
        @JvmStatic
        fun setWarp(name: String, location: Location): Warp? {
            val warp = getWarp(name)
            if (warp == null) {
                Warp.new {
                    this.name = name
                    this.world = location.world.name
                    this.x = location.x
                    this.y = location.y
                    this.z = location.z
                    this.pitch = location.pitch
                    this.yaw = location.yaw
                }
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
         * Sets a new kit with a [name] that players can equip.
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
                val newKit = Kit.new {
                    this.name = name
                    this.cost = cost
                    this.cooldown = cooldown
                }

                items.forEach { (slot, item) ->
                    KitItem.new {
                        this.kit = newKit
                        this.slot = slot
                        this.material = item.type
                        this.data = item.durability
                        this.amount = item.amount
                    }
                }
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