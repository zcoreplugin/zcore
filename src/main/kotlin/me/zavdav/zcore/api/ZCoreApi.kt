package me.zavdav.zcore.api

import me.zavdav.zcore.ZCore
import me.zavdav.zcore.api.economy.BankAccount
import me.zavdav.zcore.api.punishment.BanList
import me.zavdav.zcore.api.punishment.IpBanList
import me.zavdav.zcore.api.punishment.MuteList
import me.zavdav.zcore.api.user.OfflineUser
import me.zavdav.zcore.api.user.User
import org.bukkit.Location
import org.bukkit.World
import java.util.UUID

/** Represents the ZCore API. */
interface ZCoreApi {

    /** The current instance of ZCore. */
    val INSTANCE: ZCore

    /** The current version of ZCore. */
    val version: String

    /** A [Set] of all currently online users. */
    val onlineUsers: Set<User>

    /** A [Set] of all users that have played on the server. */
    val users: Set<OfflineUser>

    /** A list of all muted users. */
    val muteList: MuteList

    /** A list of all banned UUIDs. */
    val banList: BanList

    /** A list of all banned IPv4 addresses. */
    val ipBanList: IpBanList

    /** A map of all world spawn locations. */
    val worldSpawns: Map<World, Location>

    /** A map of all warp locations. */
    val warps: Map<String, Location>

    /** Gets an online user by their [uuid]. */
    fun getUser(uuid: UUID): User

    /** Gets an online user by their [name]. */
    fun getUser(name: String): User

    /** Gets an offline user by their [uuid]. */
    fun getOfflineUser(uuid: UUID): OfflineUser

    /** Gets an offline user by their [name]. */
    fun getOfflineUser(name: String): OfflineUser

    /** Gets a bank account by its [uuid]. */
    fun getBankAccount(uuid: UUID): BankAccount

    /** Gets a bank account by its [name]. */
    fun getBankAccount(name: String): BankAccount

    /** Gets the spawn location of a [world]. */
    fun getWorldSpawn(world: World): Location

    /** Sets the spawn [location] of a [world]. */
    fun setWorldSpawn(world: World, location: Location)

    /** Gets the location of a warp by its [name]. */
    fun getWarp(name: String): Location

    /** Sets a [location] as a new warp with a [name]. */
    fun setWarp(name: String, location: Location)

    /** Deletes the warp with the specified [name]. */
    fun deleteWarp(name: String)

}