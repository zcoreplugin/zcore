package me.zavdav.zcore.internal.model

import org.bukkit.Material
import org.bukkit.entity.CreatureType
import org.jetbrains.exposed.sql.Table

internal object OfflineUsers : Table("offline_users") {
    val id = uuid("id")
    val name = varchar("name", 16).uniqueIndex()
    val nickname = varchar("nickname", 255).nullable()
    val firstJoin = long("first_join")
    val lastJoin = long("last_join")
    val lastOnline = long("last_online")
    val accountId = uuid("account_id") references Accounts.id
    val invincible = bool("invincible")
    val vanished = bool("vanished")
    val socialspy = bool("socialspy")

    override val primaryKey = PrimaryKey(id)
}

internal object Homes : Table("homes") {
    val userId = uuid("user_id") references OfflineUsers.id
    val name = varchar("name", 255)
    val locationId = uuid("location_id") references Locations.id

    override val primaryKey = PrimaryKey(userId, name)
}

internal object Mail : Table("mail") {
    val id = uuid("id").autoGenerate()
    val sourceId = uuid("source_id") references OfflineUsers.id
    val recipientId = uuid("recipient_id") references OfflineUsers.id
    val message = text("message")

    override val primaryKey = PrimaryKey(id)
}

internal object Ignores : Table("ignores") {
    val userId = uuid("user_id") references OfflineUsers.id
    val targetId = uuid("target_id") references OfflineUsers.id

    override val primaryKey = PrimaryKey(userId, targetId)
}

internal object Accounts : Table("accounts") {
    val id = uuid("id").autoGenerate()
    val ownerId = uuid("owner_id") references OfflineUsers.id
    val balance = decimal("balance", Int.MAX_VALUE, 10)
    val overdrawLimit = decimal("overdraw_limit", Int.MAX_VALUE, 10)

    override val primaryKey = PrimaryKey(id)
}

internal object BankAccounts : Table("bank_accounts") {
    val id = uuid("id") references Accounts.id
    val name = varchar("name", 255)

    override val primaryKey = PrimaryKey(id)
}

internal object BankAccountUsers : Table("bank_account_users") {
    val bankId = uuid("bank_id") references BankAccounts.id
    val userId = uuid("user_id") references OfflineUsers.id

    override val primaryKey = PrimaryKey(bankId, userId)
}

internal object Statistics : Table("statistics") {
    val userId = uuid("user_id") references OfflineUsers.id
    val playtime = long("playtime")
    val blocksTraveled = decimal("blocks_traveled", Int.MAX_VALUE, 10)
    val damageDealt = long("damage_dealt")
    val damageTaken = long("damage_taken")
    val deaths = long("deaths")

    override val primaryKey = PrimaryKey(userId)
}

internal object BlocksPlaced : Table("blocks_placed") {
    val userId = uuid("user_id") references Statistics.userId
    val material = enumeration<Material>("material")
    val amount = long("amount")

    override val primaryKey = PrimaryKey(userId, material)
}

internal object BlocksBroken : Table("blocks_broken") {
    val userId = uuid("user_id") references Statistics.userId
    val material = enumeration<Material>("material")
    val amount = long("amount")

    override val primaryKey = PrimaryKey(userId, material)
}

internal object ItemsDropped : Table("items_dropped") {
    val userId = uuid("user_id") references Statistics.userId
    val material = enumeration<Material>("material")
    val amount = long("amount")

    override val primaryKey = PrimaryKey(userId, material)
}

internal object UsersKilled : Table("users_killed") {
    val userId = uuid("user_id") references Statistics.userId
    val targetId = uuid("target_id") references OfflineUsers.id
    val amount = long("amount")

    override val primaryKey = PrimaryKey(userId, targetId)
}

internal object MobsKilled : Table("mobs_killed") {
    val userId = uuid("user_id") references Statistics.userId
    val creature = enumeration<CreatureType>("creature")
    val amount = long("amount")

    override val primaryKey = PrimaryKey(userId, creature)
}

internal object Punishments : Table("punishments") {
    val id = uuid("id").autoGenerate()
    val issuerId = uuid("issuer_id") references OfflineUsers.id
    val timeIssued = long("time_issued")
    val duration = long("duration").nullable()
    val reason = text("reason")
    val active = bool("active")

    override val primaryKey = PrimaryKey(id)
}

internal object Mutes : Table("mutes") {
    val id = uuid("id") references Punishments.id
    val userId = uuid("user_id") references OfflineUsers.id

    override val primaryKey = PrimaryKey(id)
}

internal object Bans : Table("bans") {
    val id = uuid("id") references Punishments.id
    val uuid = uuid("uuid")

    override val primaryKey = PrimaryKey(id)
}

internal object IpBans : Table("ip_bans") {
    val id = uuid("id") references Punishments.id
    val ipAddress = varchar("ip_address", 15)

    override val primaryKey = PrimaryKey(id)
}

internal object IpBanUuids : Table("ip_ban_uuids") {
    val ipBanId = uuid("ip_ban_id") references IpBans.id
    val uuid = uuid("uuid")

    override val primaryKey = PrimaryKey(ipBanId, uuid)
}

internal object Locations : Table("locations") {
    val id = uuid("id").autoGenerate()
    val world = varchar("world", 255)
    val x = double("x")
    val y = double("y")
    val z = double("z")
    val pitch = float("pitch")
    val yaw = float("yaw")

    override val primaryKey = PrimaryKey(id)
}

internal object WorldSpawns : Table("world_spawns") {
    val name = varchar("name", 255)
    val locationId = uuid("location_id") references Locations.id

    override val primaryKey = PrimaryKey(name)
}

internal object Warps : Table("warps") {
    val name = varchar("name", 255)
    val locationId = uuid("location_id") references Locations.id

    override val primaryKey = PrimaryKey(name)
}

internal object Kits : Table("kits") {
    val id = uuid("id").autoGenerate()
    val name = varchar("name", 255).uniqueIndex()
    val cost = decimal("cost", Int.MAX_VALUE, 10)
    val cooldown = long("cooldown")

    override val primaryKey = PrimaryKey(id)
}

internal object KitItems : Table("kit_items") {
    val kitId = uuid("kit_id") references Kits.id
    val slot = integer("slot")
    val material = enumeration<Material>("material")
    val data = integer("data")
    val amount = integer("amount")

    override val primaryKey = PrimaryKey(kitId, slot)
}