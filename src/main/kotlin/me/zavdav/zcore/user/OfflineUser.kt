package me.zavdav.zcore.user

import me.zavdav.zcore.data.Accounts
import me.zavdav.zcore.data.Homes
import me.zavdav.zcore.data.Ignores
import me.zavdav.zcore.data.Mails
import me.zavdav.zcore.data.OfflineUsers
import me.zavdav.zcore.economy.BankAccount
import me.zavdav.zcore.economy.UserAccount
import me.zavdav.zcore.event.UsernameChangeEvent
import me.zavdav.zcore.location.Home
import org.bukkit.Bukkit
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

/** Represents an offline user that has played on the server before. */
sealed class OfflineUser(id: EntityID<UUID>) : UUIDEntity(id) {

    internal companion object : UUIDEntityClass<OfflineUser>(OfflineUsers) {
        fun new(uuid: UUID, name: String): OfflineUser {
            val now = System.currentTimeMillis()
            return new(uuid) {
                this._name = name
                this.firstJoin = now
                this.lastJoin = now
                this.lastOnline = now
                this.account = UserAccount.new(this)
            }
        }
    }

    /** This user's UUID. */
    val uuid: UUID get() = id.value

    private var _name: String by OfflineUsers.name

    /** This user's username. */
    var name: String get() = _name
        internal set(value) {
            if (this is User && _name != value) {
                Bukkit.getPluginManager().callEvent(UsernameChangeEvent(this, _name, value))
            }
            _name = value
        }

    /** This user's nickname. Can be `null` if this user has no nickname. */
    var nickname: String? by OfflineUsers.nickname

    /** The timestamp of this user's first join. */
    var firstJoin: Long by OfflineUsers.firstJoin
        internal set

    /** The timestamp of this user's last join. */
    var lastJoin: Long by OfflineUsers.lastJoin
        internal set

    /** The timestamp of when this user was last online. */
    var lastOnline: Long by OfflineUsers.lastOnline
        internal set

    /** This user's account where their balance is stored. */
    var account by UserAccount referencedOn OfflineUsers.account
        private set

    /** The bank accounts that this user owns. */
    val bankAccounts by BankAccount referrersOn Accounts.owner

    /** This user's homes. */
    val homes by Home referrersOn Homes.user

    /** This user's mail. */
    val mail by Mail referrersOn Mails.recipient

    /** The users that this user is ignoring. */
    val ignoredUsers by OfflineUser via Ignores

    /** Determines if this user is invincible. */
    var invincible: Boolean by OfflineUsers.invincible

    /** Determines if this user is vanished. */
    var vanished: Boolean by OfflineUsers.vanished

    /** Determines if this user can see chat messages. */
    var chatEnabled: Boolean by OfflineUsers.chatEnabled

    /** Determines if this user can see social interactions by others. */
    var socialspy: Boolean by OfflineUsers.socialspy

    /** This user's playtime in milliseconds. */
    var playtime: Long by OfflineUsers.playtime
        internal set

    /** The amount of blocks this user has placed. */
    var blocksPlaced: Long by OfflineUsers.blocksPlaced
        internal set

    /** The amount of blocks this user has broken. */
    var blocksBroken: Long by OfflineUsers.blocksBroken
        internal set

    /** The amount of blocks this user has traveled. */
    var blocksTraveled: BigDecimal by OfflineUsers.blocksTraveled
        internal set

    /** The amount of damage this user has dealt to entities. */
    var damageDealt: Long by OfflineUsers.damageDealt
        internal set

    /** The amount of damage this user has taken. */
    var damageTaken: Long by OfflineUsers.damageTaken
        internal set

    /** The amount of times this user has killed other users. */
    var usersKilled: Long by OfflineUsers.usersKilled
        internal set

    /** The amount of mobs this user has killed. */
    var mobsKilled: Long by OfflineUsers.mobsKilled
        internal set

    /** The amount of times this user has perished. */
    var deaths: Long by OfflineUsers.deaths
        internal set

    /** Gets the location of a home by its [name], or `null` if no home with this name exists. */
    fun getHome(name: String): Home? =
        Home.find {
            (Homes.user eq id) and (Homes.name.lowerCase() eq name.lowercase())
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

    /** Adds a [message] from a [sender] to this user's mail. */
    fun addMail(sender: OfflineUser, message: String) {
        Mail.new(sender, this, message)
    }

    /** Clears this user's mail. */
    fun clearMail() {
        Mails.deleteWhere { Mails.recipient eq this@OfflineUser.id }
    }

    /**
     * Makes this user ignore a [user].
     * Returns `true` on success, `false` if that user is already ignored.
     */
    fun addIgnore(user: OfflineUser): Boolean {
        val notIgnored = user !in ignoredUsers
        if (notIgnored) {
            Ignores.insert {
                it[this.user] = this@OfflineUser.id
                it[target] = user.id
            }
        }
        return notIgnored
    }

    /**
     * Makes this user stop ignoring a [user].
     * Returns `true` on success, `false` if that user is not ignored.
     */
    fun removeIgnore(user: OfflineUser): Boolean {
        val ignored = user in ignoredUsers
        if (ignored) {
            Ignores.deleteWhere { (Ignores.user eq this@OfflineUser.id) and (Ignores.target eq user.id) }
        }
        return ignored
    }

}