package me.zavdav.zcore

import me.zavdav.zcore.api.ZCoreApi
import me.zavdav.zcore.api.economy.BankAccount
import me.zavdav.zcore.api.punishment.BanList
import me.zavdav.zcore.api.punishment.IpBanList
import me.zavdav.zcore.api.punishment.MuteList
import me.zavdav.zcore.api.user.OfflineUser
import me.zavdav.zcore.api.user.User
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.plugin.java.JavaPlugin
import java.util.UUID

/** The main class of the ZCore plugin. */
class ZCore : JavaPlugin() {

    override fun onDisable() { }

    override fun onEnable() { }

    /** Represents the ZCore API. */
    companion object Api : ZCoreApi {

        @JvmStatic
        override val INSTANCE: ZCore
            get() = TODO("Not yet implemented")

        @JvmStatic
        override val version: String
            get() = TODO("Not yet implemented")

        @JvmStatic
        override val onlineUsers: Set<User>
            get() = TODO("Not yet implemented")

        @JvmStatic
        override val users: Set<OfflineUser>
            get() = TODO("Not yet implemented")

        @JvmStatic
        override val muteList: MuteList
            get() = TODO("Not yet implemented")

        @JvmStatic
        override val banList: BanList
            get() = TODO("Not yet implemented")

        @JvmStatic
        override val ipBanList: IpBanList
            get() = TODO("Not yet implemented")

        @JvmStatic
        override val worldSpawns: Map<World, Location>
            get() = TODO("Not yet implemented")

        @JvmStatic
        override val warps: Map<String, Location>
            get() = TODO("Not yet implemented")

        @JvmStatic
        override fun getUser(uuid: UUID): User {
            TODO("Not yet implemented")
        }

        @JvmStatic
        override fun getUser(name: String): User {
            TODO("Not yet implemented")
        }

        @JvmStatic
        override fun getOfflineUser(uuid: UUID): OfflineUser {
            TODO("Not yet implemented")
        }

        @JvmStatic
        override fun getOfflineUser(name: String): OfflineUser {
            TODO("Not yet implemented")
        }

        @JvmStatic
        override fun getBankAccount(uuid: UUID): BankAccount {
            TODO("Not yet implemented")
        }

        @JvmStatic
        override fun getBankAccount(name: String): BankAccount {
            TODO("Not yet implemented")
        }

        @JvmStatic
        override fun getWorldSpawn(world: World): Location {
            TODO("Not yet implemented")
        }

        @JvmStatic
        override fun setWorldSpawn(world: World, location: Location) {
            TODO("Not yet implemented")
        }

        @JvmStatic
        override fun getWarp(name: String): Location {
            TODO("Not yet implemented")
        }

        @JvmStatic
        override fun setWarp(name: String, location: Location) {
            TODO("Not yet implemented")
        }

        @JvmStatic
        override fun deleteWarp(name: String) {
            TODO("Not yet implemented")
        }

    }

}