package me.zavdav.zcore.api

import me.zavdav.zcore.ZCore
import me.zavdav.zcore.api.economy.BankAccount
import me.zavdav.zcore.api.punishments.BanList
import me.zavdav.zcore.api.punishments.IpBanList
import me.zavdav.zcore.api.punishments.MuteList
import me.zavdav.zcore.api.user.OfflineUser
import me.zavdav.zcore.api.user.User
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

}