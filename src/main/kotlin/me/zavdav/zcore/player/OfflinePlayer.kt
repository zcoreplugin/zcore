package me.zavdav.zcore.player

import me.zavdav.zcore.data.Accounts
import me.zavdav.zcore.data.Homes
import me.zavdav.zcore.data.Ignores
import me.zavdav.zcore.data.Mails
import me.zavdav.zcore.data.OfflinePlayers
import me.zavdav.zcore.economy.BankAccount
import me.zavdav.zcore.economy.PersonalAccount
import me.zavdav.zcore.location.Home
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.lowerCase
import java.math.BigDecimal
import java.util.UUID

/** Represents an offline player that has played before. */
sealed class OfflinePlayer(id: EntityID<UUID>) : UUIDEntity(id) {

    internal companion object : UUIDEntityClass<OfflinePlayer>(OfflinePlayers) {
        fun new(uuid: UUID, name: String): OfflinePlayer {
            val now = System.currentTimeMillis()
            return new(uuid) {
                this.name = name
                this.firstJoin = now
                this.lastJoin = now
                this.lastOnline = now
                this.account = PersonalAccount.new(this)
            }
        }
    }

    /** This player's UUID. */
    val uuid: UUID get() = id.value

    /** This player's username. */
    var name: String by OfflinePlayers.name
        internal set

    /** This player's nickname. Can be `null` if this player has no nickname. */
    var nickname: String? by OfflinePlayers.nickname

    /** The timestamp of this player's first join. */
    var firstJoin: Long by OfflinePlayers.firstJoin
        private set

    /** The timestamp of this player's last join. */
    var lastJoin: Long by OfflinePlayers.lastJoin
        internal set

    /** The timestamp of when this player was last online. */
    var lastOnline: Long by OfflinePlayers.lastOnline
        internal set

    /** This player's account where their balance is stored. */
    var account by PersonalAccount referencedOn OfflinePlayers.account
        private set

    /** The bank accounts that this player owns. */
    val bankAccounts by BankAccount referrersOn Accounts.owner

    /** This player's homes. */
    val homes by Home referrersOn Homes.player

    /** This player's mail. */
    val mail by Mail referrersOn Mails.recipient

    /** The players that this player is ignoring. */
    val ignoredPlayers by OfflinePlayer via Ignores

    /** Determines if this player is invincible. */
    var invincible: Boolean by OfflinePlayers.invincible

    /** Determines if this player is vanished. */
    var vanished: Boolean by OfflinePlayers.vanished

    /** Determines if this player can see chat messages. */
    var chatEnabled: Boolean by OfflinePlayers.chatEnabled

    /** Determines if this player can see social interactions by others. */
    var socialspy: Boolean by OfflinePlayers.socialspy

    /** This player's playtime in milliseconds. */
    var playtime: Long by OfflinePlayers.playtime
        internal set

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
            Home.new(
                this,
                name,
                location.world.name,
                location.x,
                location.y,
                location.z,
                location.pitch,
                location.yaw
            )
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

    /** Adds a [message] from a [sender] to this player's mail. */
    fun addMail(sender: OfflinePlayer, message: String) {
        Mail.new(sender, this, message)
    }

    /** Clears this player's mail. */
    fun clearMail() {
        Mails.deleteWhere { Mails.recipient eq this@OfflinePlayer.id }
    }

    /**
     * Makes this player ignore a [player].
     * Returns `true` on success, `false` if that player is already ignored.
     */
    fun addIgnore(player: OfflinePlayer): Boolean {
        val notIgnored = player !in ignoredPlayers
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
        val ignored = player in ignoredPlayers
        if (ignored) {
            Ignores.deleteWhere { (Ignores.player eq this@OfflinePlayer.id) and (Ignores.target eq player.id) }
        }
        return ignored
    }

}