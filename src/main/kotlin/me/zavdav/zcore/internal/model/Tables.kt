package me.zavdav.zcore.internal.model

import org.bukkit.Material
import org.bukkit.entity.CreatureType
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption.CASCADE
import org.jetbrains.exposed.sql.Table
import java.math.BigDecimal
import java.util.UUID

internal open class UUIDTable(name: String) : IdTable<UUID>(name) {
    override val id: Column<EntityID<UUID>> = uuid("id").autoGenerate().entityId()
    final override val primaryKey = PrimaryKey(id)
}

internal object OfflineUsers : UUIDTable("offline_users") {
    val name = varchar("name", 16).uniqueIndex()
    val nickname = varchar("nickname", 255).nullable().default(null)
    val firstJoin = long("first_join")
    val lastJoin = long("last_join")
    val lastOnline = long("last_online")
    val account = reference("account", Accounts, CASCADE, CASCADE)
    val invincible = bool("invincible").default(false)
    val vanished = bool("vanished").default(false)
    val chatEnabled = bool("chat_enabled").default(true)
    val socialspy = bool("socialspy").default(false)
}

internal object Mail : UUIDTable("mail") {
    val sender = reference("sender", OfflineUsers, CASCADE, CASCADE)
    val recipient = reference("recipient", OfflineUsers, CASCADE, CASCADE)
    val message = text("message")
}

internal object Ignores : Table("ignores") {
    val user = reference("user", OfflineUsers, CASCADE, CASCADE)
    val target = reference("target", OfflineUsers, CASCADE, CASCADE)

    override val primaryKey = PrimaryKey(user, target)
}

internal object Accounts : UUIDTable("accounts") {
    val owner = reference("owner", OfflineUsers, CASCADE, CASCADE)
    val balance = decimal("balance", Int.MAX_VALUE, 10).default(BigDecimal.ZERO)
    val overdrawLimit = decimal("overdraw_limit", Int.MAX_VALUE, 10).default(BigDecimal.ZERO)
}

internal object BankAccounts : UUIDTable("bank_accounts") {
    override val id = reference("id", Accounts, CASCADE, CASCADE)
    val name = varchar("name", 255)
}

internal object BankAccountUsers : Table("bank_account_users") {
    val bank = reference("bank", BankAccounts, CASCADE, CASCADE)
    val user = reference("user", OfflineUsers, CASCADE, CASCADE)

    override val primaryKey = PrimaryKey(bank, user)
}

internal object Statistics : UUIDTable("statistics") {
    override val id = reference("id", OfflineUsers, CASCADE, CASCADE)
    val playtime = long("playtime").default(0)
    val blocksTraveled = decimal("blocks_traveled", Int.MAX_VALUE, 10).default(BigDecimal.ZERO)
    val damageDealt = long("damage_dealt").default(0)
    val damageTaken = long("damage_taken").default(0)
    val deaths = long("deaths").default(0)
}

internal object BlocksPlaced : Table("blocks_placed") {
    val user = reference("user", Statistics, CASCADE, CASCADE)
    val material = enumeration<Material>("material")
    val amount = long("amount").default(0)

    override val primaryKey = PrimaryKey(user, material)
}

internal object BlocksBroken : Table("blocks_broken") {
    val user = reference("user", Statistics, CASCADE, CASCADE)
    val material = enumeration<Material>("material")
    val amount = long("amount").default(0)

    override val primaryKey = PrimaryKey(user, material)
}

internal object ItemsDropped : Table("items_dropped") {
    val user = reference("user", Statistics, CASCADE, CASCADE)
    val material = enumeration<Material>("material")
    val amount = long("amount").default(0)

    override val primaryKey = PrimaryKey(user, material)
}

internal object UsersKilled : Table("users_killed") {
    val user = reference("user", Statistics, CASCADE, CASCADE)
    val target = reference("target", OfflineUsers, CASCADE, CASCADE)
    val amount = long("amount").default(0)

    override val primaryKey = PrimaryKey(user, target)
}

internal object MobsKilled : Table("mobs_killed") {
    val user = reference("user", Statistics, CASCADE, CASCADE)
    val creature = enumeration<CreatureType>("creature")
    val amount = long("amount").default(0)

    override val primaryKey = PrimaryKey(user, creature)
}

internal object Punishments : UUIDTable("punishments") {
    val issuer = reference("issuer", OfflineUsers, CASCADE, CASCADE)
    val timeIssued = long("time_issued")
    val duration = long("duration").nullable()
    val reason = text("reason")
    val active = bool("active").default(true)
}

internal object Mutes : UUIDTable("mutes") {
    override val id = reference("id", Punishments, CASCADE, CASCADE)
    val user = reference("user", OfflineUsers, CASCADE, CASCADE)
}

internal object Bans : UUIDTable("bans") {
    override val id = reference("id", Punishments, CASCADE, CASCADE)
    val uuid = uuid("uuid")
}

internal object IpBans : UUIDTable("ip_bans") {
    override val id = reference("id", Punishments, CASCADE, CASCADE)
    val ipAddress = varchar("ip_address", 15)
}

internal object IpBanUuids : Table("ip_ban_uuids") {
    val ipBan = reference("ip_ban", IpBans, CASCADE, CASCADE)
    val uuid = uuid("uuid")

    override val primaryKey = PrimaryKey(ipBan, uuid)
}

internal enum class LocationType { SPAWNPOINT, WARP, HOME }

internal object NamedLocations : UUIDTable("named_locations") {
    val name = varchar("name", 255)
    val type = enumeration<LocationType>("type")
    val world = varchar("world", 255)
    val x = double("x")
    val y = double("y")
    val z = double("z")
    val pitch = float("pitch")
    val yaw = float("yaw")

    init { uniqueIndex(name, type) }
}

internal object Kits : UUIDTable("kits") {
    val name = varchar("name", 255).uniqueIndex()
    val cost = decimal("cost", Int.MAX_VALUE, 10).default(BigDecimal.ZERO)
    val cooldown = long("cooldown").default(0)
}

internal object KitItems : Table("kit_items") {
    val kit = reference("kit", Kits, CASCADE, CASCADE)
    val slot = integer("slot")
    val material = enumeration<Material>("material")
    val data = integer("data")
    val amount = integer("amount")

    override val primaryKey = PrimaryKey(kit, slot)
}