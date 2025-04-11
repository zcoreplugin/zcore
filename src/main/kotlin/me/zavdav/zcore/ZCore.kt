package me.zavdav.zcore

import me.zavdav.zcore.economy.BankAccount
import me.zavdav.zcore.kit.Kit
import me.zavdav.zcore.util.NamedLocation
import me.zavdav.zcore.punishment.BanList
import me.zavdav.zcore.punishment.IpBanList
import me.zavdav.zcore.punishment.MuteList
import me.zavdav.zcore.user.OfflineUser
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.checkAndPut
import me.zavdav.zcore.util.checkAndRemove
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.plugin.java.JavaPlugin
import java.util.UUID

/** The main class of the ZCore plugin. */
class ZCore : JavaPlugin() {

    override fun onEnable() {
        INSTANCE = this
    }

    override fun onDisable() { }

    /** Represents the ZCore API. */
    companion object Api {

        // Backing fields
        private val _worldSpawns = mutableMapOf<String, NamedLocation>()
        private val _warps = mutableMapOf<String, NamedLocation>()
        private val _kits = mutableMapOf<String, Kit>()
        private val _bankAccounts = mutableMapOf<UUID, BankAccount>()

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
        val version: String
            get() = INSTANCE.description.version

        /** A [Set] of all currently online users. */
        @JvmStatic
        val onlineUsers: Set<User>
            get() = TODO("Not yet implemented")

        /** A [Set] of all users that have played on the server. */
        @JvmStatic
        val users: Set<OfflineUser>
            get() = TODO("Not yet implemented")

        /** A list of all muted users. */
        @JvmStatic
        val muteList = MuteList()

        /** A list of all banned UUIDs. */
        @JvmStatic
        val banList = BanList()

        /** A list of all banned IPv4 addresses. */
        @JvmStatic
        val ipBanList = IpBanList()

        /** A map of all world spawn locations. */
        @JvmStatic
        val worldSpawns: Map<String, NamedLocation> get() = _worldSpawns

        /** A map of all warp locations. */
        @JvmStatic
        val warps: Map<String, NamedLocation> get() = _warps

        /** A map of all kits. */
        @JvmStatic
        val kits: Map<String, Kit> get() = _kits

        /** A map of all bank accounts. */
        @JvmStatic
        val bankAccounts: Map<UUID, BankAccount> get() = _bankAccounts

        /** Gets an online user by their [uuid], or `null` if no such user exists. */
        @JvmStatic
        fun getUser(uuid: UUID): User? =
            onlineUsers.find { it.uuid == uuid }

        /** Gets an online user by their [name], or `null` if no such user exists. */
        @JvmStatic
        fun getUser(name: String): User? =
            onlineUsers.find { it.name.equals(name, true) }

        /** Gets an offline user by their [uuid], or `null` if no such user exists. */
        @JvmStatic
        fun getOfflineUser(uuid: UUID): OfflineUser? =
            users.find { it.uuid == uuid }

        /** Gets an offline user by their [name], or `null` if no such user exists. (case-sensitive) */
        @JvmStatic
        fun getOfflineUser(name: String): OfflineUser? =
            users.find { it.name == name }

        /** Gets a bank account by its [uuid], or `null` if no such bank account exists. */
        @JvmStatic
        fun getBankAccount(uuid: UUID): BankAccount? = _bankAccounts[uuid]

        /** Gets a bank account by its [name], or `null` if no such bank account exists. */
        @JvmStatic
        fun getBankAccount(name: String): BankAccount? =
            _bankAccounts.values.find { it.name.equals(name, true) }

        /** Gets the spawn location of a [world], or `null` if this world does not exist. */
        @JvmStatic
        fun getWorldSpawn(world: World): NamedLocation? =
            _worldSpawns[world.name.lowercase()]

        /** Sets the spawn [location] of a [world]. */
        @JvmStatic
        fun setWorldSpawn(world: World, location: Location) {
            world.setSpawnLocation(location.x.toInt(), location.y.toInt(), location.z.toInt())
            _worldSpawns[world.name.lowercase()] = NamedLocation(world.name, location)
        }

        /** Gets the location of a warp by its [name], or `null` if no such warp exists. */
        @JvmStatic
        fun getWarp(name: String): NamedLocation? = _warps[name.lowercase()]

        /**
         * Sets a new warp with a [name] and a [location].
         * Returns `false` if a warp with this name already exists.
         */
        @JvmStatic
        fun setWarp(name: String, location: Location): Boolean =
            _warps.checkAndPut(name.lowercase(), NamedLocation(name, location))

        /**
         * Deletes the warp with the specified [name].
         * Returns `false` if no warp with this name exists.
         */
        @JvmStatic
        fun deleteWarp(name: String): Boolean =
            _warps.checkAndRemove(name.lowercase())

        /** Gets a kit by its [name], or `null` if no such kit exists. */
        @JvmStatic
        fun getKit(name: String): Kit? = _kits[name.lowercase()]

        /**
         * Sets a new [kit] that users can equip.
         * Returns `false` if a kit with this name already exists.
         */
        @JvmStatic
        fun setKit(kit: Kit): Boolean =
            _kits.checkAndPut(kit.name.lowercase(), kit)

        /**
         * Deletes the kit with the specified [name].
         * Returns `false` if no kit with this name exists.
         */
        @JvmStatic
        fun deleteKit(name: String): Boolean =
            _kits.checkAndRemove(name.lowercase())

    }

}