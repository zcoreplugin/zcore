package me.zavdav.zcore

import me.zavdav.zcore.command.*
import me.zavdav.zcore.config.ZCoreConfig
import me.zavdav.zcore.data.BankAccounts
import me.zavdav.zcore.data.BankMembers
import me.zavdav.zcore.data.Bans
import me.zavdav.zcore.data.Homes
import me.zavdav.zcore.data.Ignores
import me.zavdav.zcore.data.IpBans
import me.zavdav.zcore.data.KitItems
import me.zavdav.zcore.data.Kits
import me.zavdav.zcore.data.Mails
import me.zavdav.zcore.data.Mutes
import me.zavdav.zcore.data.OfflinePlayers
import me.zavdav.zcore.data.PersonalAccounts
import me.zavdav.zcore.data.Warps
import me.zavdav.zcore.economy.BankAccount
import me.zavdav.zcore.event.ActionListener
import me.zavdav.zcore.event.ActivityListener
import me.zavdav.zcore.event.JoinQuitListener
import me.zavdav.zcore.event.StatisticsListener
import me.zavdav.zcore.kit.Kit
import me.zavdav.zcore.kit.KitItem
import me.zavdav.zcore.location.Warp
import me.zavdav.zcore.permission.ValuePermissions
import me.zavdav.zcore.player.CorePlayer
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.player.core
import me.zavdav.zcore.util.Materials
import me.zavdav.zcore.util.getField
import me.zavdav.zcore.version.ZCoreVersion
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.RegisteredListener
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.math.BigDecimal
import java.math.RoundingMode
import java.sql.Connection
import java.text.NumberFormat
import java.util.Locale
import java.util.SortedSet
import java.util.UUID

/** The main class of the ZCore plugin. */
class ZCore : JavaPlugin() {

    private lateinit var transaction: Transaction

    override fun onEnable() {
        INSTANCE = this

        server.logger.info("[ZCore] Loading configuration...")
        ZCoreConfig.load()
        Materials.load()
        ValuePermissions.load()

        server.logger.info("[ZCore] Establishing database connection...")
        Database.connect("jdbc:h2:${dataFolder.absolutePath}/db/zcore", "org.h2.Driver")
        transaction = TransactionManager.currentOrNew(Connection.TRANSACTION_REPEATABLE_READ)

        SchemaUtils.create(
            OfflinePlayers,
            PersonalAccounts,
            BankAccounts,
            BankMembers,
            Mutes,
            Bans,
            IpBans,
            Homes,
            Warps,
            Kits,
            KitItems,
            Mails,
            Ignores
        )

        server.logger.info("[ZCore] Registering commands...")
        CommandDispatcher.registerAll()

        server.logger.info("[ZCore] Registering event listeners...")
        server.pluginManager.registerEvents(ActionListener(), this)
        server.pluginManager.registerEvents(ActivityListener(), this)
        server.pluginManager.registerEvents(JoinQuitListener(), this)
        server.pluginManager.registerEvents(StatisticsListener(), this)

        server.logger.info("[ZCore] Running version $version")
    }

    override fun onDisable() {
        server.logger.info("[ZCore] Unregistering commands...")
        CommandDispatcher.unregisterAll()

        server.logger.info("[ZCore] Unregistering event listeners...")
        val listeners = getField<MutableMap<Event.Type, SortedSet<RegisteredListener>>>(
            server.pluginManager, "listeners")
        listeners.entries.forEach { (_, set) -> set.removeIf { it.plugin is ZCore } }

        server.logger.info("[ZCore] Terminating database connection...")
        transaction.commit()
        transaction.close()

        server.logger.info("[ZCore] Saving configuration...")
        ValuePermissions.save()
    }

    /** Represents the ZCore API. */
    companion object Api {

        /** The current instance of ZCore. */
        @JvmStatic
        lateinit var INSTANCE: ZCore
            private set

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

        /** Gets a player by their [uuid], or `null` if no such player is online. */
        @JvmStatic
        fun getPlayer(uuid: UUID): CorePlayer? =
            Bukkit.getOnlinePlayers().firstOrNull { it.uniqueId == uuid }?.core()

        /** Gets a player by their [name], or `null` if no such player is online. */
        @JvmStatic
        fun getPlayer(name: String): CorePlayer? =
            Bukkit.getOnlinePlayers().firstOrNull { it.name.equals(name, true) }?.core()

        /**
         * Gets all players whose names match a [partialName].
         * If an exact match is found, a list with only that match is returned,
         * if no matches are found, an empty list is returned.
         */
        @JvmStatic
        fun matchPlayer(partialName: String): List<CorePlayer> {
            val matches = mutableListOf<CorePlayer>()

            for (player in Bukkit.getOnlinePlayers()) {
                val name = player.name
                if (partialName.equals(name, true)) {
                    matches.clear()
                    matches.add(player.core())
                    break
                }

                if (player.name.startsWith(partialName, true)) {
                    matches.add(player.core())
                }
            }

            return matches
        }

        /** Gets an offline player by their [uuid], or `null` if no such player exists. */
        @JvmStatic
        fun getOfflinePlayer(uuid: UUID): OfflinePlayer? =
            OfflinePlayer.findById(uuid)

        /** Gets an offline player by their [name], or `null` if no such player exists. */
        @JvmStatic
        fun getOfflinePlayer(name: String): OfflinePlayer? =
            OfflinePlayer.find { OfflinePlayers.name.lowerCase() eq name.lowercase() }.firstOrNull()

        /** Gets a bank by its [uuid], or `null` if no such bank exists. */
        @JvmStatic
        fun getBank(uuid: UUID): BankAccount? =
            BankAccount.findById(uuid)

        /** Gets a bank by its [name], or `null` if no such bank exists. */
        @JvmStatic
        fun getBank(name: String): BankAccount? =
            BankAccount.find { BankAccounts.name.lowerCase() eq name.lowercase() }.firstOrNull()

        /**
         * Creates a new bank with a [name] and an [owner].
         * Returns the created bank, or `null` if a bank with this name already exists.
         */
        @JvmStatic
        fun createBank(name: String, owner: OfflinePlayer): BankAccount? {
            if (getBank(name) != null) return null
            return BankAccount.new {
                this.name = name
                this._owner = owner
            }
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

        @JvmStatic
        fun formatCurrency(amount: BigDecimal): String {
            var roundedAmount = amount.setScale(2, RoundingMode.DOWN)

            try {
                roundedAmount = roundedAmount.setScale(0, RoundingMode.UNNECESSARY)
            } catch (_: ArithmeticException) {}

            val currencyFormat = NumberFormat.getInstance(Locale.US)
            currencyFormat.minimumFractionDigits = 0
            currencyFormat.maximumFractionDigits = roundedAmount.scale()

            return "${ZCoreConfig.getString("general.currency")}${currencyFormat.format(roundedAmount)}"
        }

    }

}