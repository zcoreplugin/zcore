package me.zavdav.zcore.player

import me.zavdav.zcore.ZCore
import me.zavdav.zcore.data.BankAccounts
import me.zavdav.zcore.data.Homes
import me.zavdav.zcore.data.Ignores
import me.zavdav.zcore.data.IpAddresses
import me.zavdav.zcore.data.Mails
import me.zavdav.zcore.data.OfflinePlayers
import me.zavdav.zcore.data.PersonalAccounts
import me.zavdav.zcore.data.PowerTools
import me.zavdav.zcore.economy.BankAccount
import me.zavdav.zcore.economy.PersonalAccount
import me.zavdav.zcore.location.Home
import me.zavdav.zcore.permission.ValuePermissions
import me.zavdav.zcore.punishment.BanList
import me.zavdav.zcore.punishment.MuteList
import org.bukkit.Bukkit
import org.bukkit.Material
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.lowerCase
import java.math.BigDecimal
import java.net.Inet4Address
import java.util.UUID

/** Represents an offline player that has played before. */
class OfflinePlayer internal constructor(id: EntityID<UUID>) : UUIDEntity(id) {

    companion object : UUIDEntityClass<OfflinePlayer>(OfflinePlayers)

    /** This player's UUID. */
    val uuid: UUID get() = id.value

    /** This player's username. */
    var name: String by OfflinePlayers.name
        internal set

    /** This player's nickname. Can be `null` if this player has no nickname. */
    var nickname: String? by OfflinePlayers.nickname

    /** The timestamp of this player's first join. */
    var firstJoin: Long by OfflinePlayers.firstJoin
        internal set

    /** The timestamp of this player's last join. */
    var lastJoin: Long by OfflinePlayers.lastJoin
        internal set

    /** The timestamp of this player's last activity. */
    var lastActivity: Long by OfflinePlayers.lastActivity
        internal set

    /** This player's account where their balance is stored. */
    val account by PersonalAccount backReferencedOn PersonalAccounts.owner

    /** The bank accounts that this player owns. */
    val bankAccounts by BankAccount referrersOn BankAccounts.owner

    /** This player's homes. */
    val homes by Home referrersOn Homes.player

    /** This player's mail. */
    val mail by Mail referrersOn Mails.recipient

    /** The players that this player is ignoring. */
    val ignoredPlayers by OfflinePlayer.via(Ignores.player, Ignores.target)

    /** This player's power tools. */
    val powerTools by PowerTool referrersOn PowerTools.player

    /** Determines if this player is invincible. */
    var isInvincible: Boolean by OfflinePlayers.isInvincible

    private var _isVanished: Boolean by OfflinePlayers.isVanished

    /** Determines if this player is vanished. */
    var isVanished: Boolean
        get() = _isVanished
        set(value) {
            _isVanished = value
            if (!isOnline) return

            for (pl in Bukkit.getOnlinePlayers()) {
                if (pl.core().data.isVanished) {
                    Bukkit.getOnlinePlayers()
                        .filter { !it.isOp && !it.hasPermission("zcore.vanish.bypass") }
                        .forEach { it.hidePlayer(pl) }
                } else {
                    Bukkit.getOnlinePlayers().forEach { it.showPlayer(pl) }
                }
            }
        }

    /** Determines if this player sees messages sent by others through /msg or /mail. */
    var isSocialSpy: Boolean by OfflinePlayers.isSocialSpy

    internal var _playtime: Long by OfflinePlayers.playtime

    /** This player's playtime in milliseconds. */
    val playtime: Long
        get() =
            if (isOnline)
                _playtime + System.currentTimeMillis() - lastJoin
            else
                _playtime

    /** The amount of blocks this player has placed. */
    var blocksPlaced: Long by OfflinePlayers.blocksPlaced
        internal set

    /** The amount of blocks this player has broken. */
    var blocksBroken: Long by OfflinePlayers.blocksBroken
        internal set

    /** The amount of blocks this player has traveled. */
    var blocksTraveled: BigDecimal by OfflinePlayers.blocksTraveled
        internal set

    /** The amount of damage this player has dealt to entities. */
    var damageDealt: Long by OfflinePlayers.damageDealt
        internal set

    /** The amount of damage this player has taken. */
    var damageTaken: Long by OfflinePlayers.damageTaken
        internal set

    /** The amount of times this player has killed other players. */
    var playersKilled: Long by OfflinePlayers.playersKilled
        internal set

    /** The amount of mobs this player has killed. */
    var mobsKilled: Long by OfflinePlayers.mobsKilled
        internal set

    /** The amount of times this player has perished. */
    var deaths: Long by OfflinePlayers.deaths
        internal set

    /** @return `true` if this player is online */
    val isOnline: Boolean get() = ZCore.getPlayer(uuid) != null

    /** @return `true` if this player is banned */
    val isBanned: Boolean get() = BanList.getActiveBan(this) != null

    /** @return `true` if this player is muted */
    val isMuted: Boolean get() = MuteList.getActiveMute(this) != null

    /** All previous IP addresses of this player. */
    val ipAddresses: List<Inet4Address>
        get() = IpAddresses.select(IpAddresses.ipAddress)
            .where { IpAddresses.player eq this@OfflinePlayer.id }
            .map { it[IpAddresses.ipAddress] }

    /** Gets the value of a [permission], or [default] if it is not set. */
    fun getPermissionValue(permission: String, default: Int): Int =
        ValuePermissions.getPermissionValue(this, permission, default)

    /** Sets the [value] of a [permission]. */
    fun setPermissionValue(permission: String, value: Int) =
        ValuePermissions.setPermissionValue(this, permission, value)

    /** Adds [delta] to the value of a [permission]. */
    fun addToPermissionValue(permission: String, delta: Int) =
        ValuePermissions.addToPermissionValue(this, permission, delta)

    /** Gets the location of a home by its [name], or `null` if no home with this name exists. */
    fun getHome(name: String): Home? =
        Home.find {
            (Homes.player eq id) and (Homes.name.lowerCase() eq name.lowercase())
        }.firstOrNull()

    /**
     * Sets a new home with a [name] and a [location].
     * Returns `null` on success, or the home with this name if it already exists.
     */
    fun setHome(name: String, location: org.bukkit.Location): Home? {
        val home = getHome(name)
        if (home == null) {
            Home.new {
                this.player = this@OfflinePlayer
                this.name = name
                this.world = location.world.name
                this.x = location.x
                this.y = location.y
                this.z = location.z
                this.pitch = location.pitch
                this.yaw = location.yaw
            }
        }
        return home
    }

    /**
     * Moves a home to a new location.
     *
     * @param name the home name
     * @param location the new location
     * @return the home if it was moved, `null` if no home with this name was found
     */
    fun moveHome(name: String, location: org.bukkit.Location): Home? {
        val home = getHome(name)
        if (home != null) {
            home.world = location.world.name
            home.x = location.x
            home.y = location.y
            home.z = location.z
            home.pitch = location.pitch
            home.yaw = location.yaw
        }
        return home
    }

    /**
     * Deletes the home with the specified [name].
     * Returns the home that was deleted, or `null` if no home with this name exists.
     */
    fun deleteHome(name: String): Home? {
        val home = getHome(name)
        home?.delete()
        return home
    }

    /** Adds a [message] to the mail of a [recipient]. */
    fun sendMail(recipient: OfflinePlayer, message: String) {
        Mail.new {
            this.sender = this@OfflinePlayer
            this.recipient = recipient
            this.message = message
        }
    }

    /**
     * Clears this player's mail.
     * Returns `true` on success, `false` if this player has no mail.
     */
    fun clearMail(): Boolean {
        if (mail.empty()) return false
        mail.forEach { it.delete() }
        return true
    }

    /**
     * Returns `true` if this player ignores a player.
     *
     * @param player the other player
     * @return `true` if this player ignores the player
     */
    fun ignores(player: OfflinePlayer) = player in ignoredPlayers

    /**
     * Makes this player ignore a [player].
     * Returns `true` on success, `false` if that player is already ignored.
     */
    fun addIgnore(player: OfflinePlayer): Boolean {
        val notIgnored = !ignores(player)
        if (notIgnored) {
            Ignores.insert {
                it[this.player] = this@OfflinePlayer.id
                it[target] = player.id
            }
        }
        return notIgnored
    }

    /**
     * Makes this player stop ignoring a [player].
     * Returns `true` on success, `false` if that player is not ignored.
     */
    fun removeIgnore(player: OfflinePlayer): Boolean {
        val ignored = ignores(player)
        if (ignored) {
            Ignores.deleteWhere { (Ignores.player eq this@OfflinePlayer.id) and (Ignores.target eq player.id) }
        }
        return ignored
    }

    /**
     * Gets a power tool by its material and data.
     *
     * @param material the item material
     * @param data the item data
     * @return the power tool, or `null` if it does not exist
     */
    fun getPowerTool(material: Material, data: Short): PowerTool? =
        powerTools.firstOrNull { it.material == material && it.data == data }

    /**
     * Sets a new power tool.
     *
     * @param material the item material
     * @param data the item data
     * @param command the command that will be executed by the power tool
     */
    fun setPowerTool(material: Material, data: Short, command: String) {
        deletePowerTool(material, data)
        PowerTool.new {
            this.player = this@OfflinePlayer
            this.material = material
            this.data = data
            this.command = command
        }
    }

    /**
     * Deletes a power tool.
     *
     * @param material the item material
     * @param data the item data
     * @return the power tool if it was deleted, `null` if it was not found
     */
    fun deletePowerTool(material: Material, data: Short): PowerTool? {
        val powerTool = getPowerTool(material, data)
        powerTool?.delete()
        return powerTool
    }

}