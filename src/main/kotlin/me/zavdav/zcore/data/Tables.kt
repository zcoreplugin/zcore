package me.zavdav.zcore.data

import org.bukkit.Material
import org.jetbrains.exposed.dao.id.CompositeIdTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption.CASCADE
import java.math.BigDecimal

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
    val playtime = long("playtime").default(0)
    val blocksPlaced = long("blocks_placed").default(0)
    val blocksBroken = long("blocks_broken").default(0)
    val blocksTraveled = decimal("blocks_traveled", 100000, 10).default(BigDecimal.ZERO)
    val damageDealt = long("damage_dealt").default(0)
    val damageTaken = long("damage_taken").default(0)
    val usersKilled = long("users_killed").default(0)
    val mobsKilled = long("mobs_killed").default(0)
    val deaths = long("deaths").default(0)
}

internal object Accounts : UUIDTable("accounts") {
    val owner = reference("owner", OfflineUsers, CASCADE, CASCADE)
    val balance = decimal("balance", 100000, 10).default(BigDecimal.ZERO)
    val overdrawLimit = decimal("overdraw_limit", 100000, 10).default(BigDecimal.ZERO)
}

internal object UserAccounts: UUIDTable("user_accounts")

internal object BankAccounts : UUIDTable("bank_accounts") {
    val name = varchar("name", 255).uniqueIndex()
}

internal object BankAccountUsers : CompositeIdTable("bank_account_users") {
    val bank = reference("bank", BankAccounts, CASCADE, CASCADE)
    val user = reference("user", OfflineUsers, CASCADE, CASCADE)

    override val primaryKey = PrimaryKey(bank, user)
}

internal object Punishments : UUIDTable("punishments") {
    val issuer = reference("issuer", OfflineUsers, CASCADE, CASCADE)
    val timeIssued = long("time_issued")
    val duration = long("duration").nullable()
    val reason = text("reason")
    val active = bool("active").default(true)
}

internal object Mutes : UUIDTable("mutes") {
    val target = reference("user", OfflineUsers, CASCADE, CASCADE)
}

internal object Bans : UUIDTable("bans") {
    val target = uuid("uuid")
}

internal object IpBans : UUIDTable("ip_bans") {
    val target = varchar("ip_address", 15)
}

internal object IpBanUuids : CompositeIdTable("ip_ban_uuids") {
    val ipBan = reference("ip_ban", IpBans, CASCADE, CASCADE)
    val uuid = uuid("uuid")

    override val primaryKey = PrimaryKey(ipBan, uuid)
}

internal object Locations : UUIDTable("locations") {
    val world = varchar("world", 255)
    val x = double("x")
    val y = double("y")
    val z = double("z")
    val pitch = float("pitch")
    val yaw = float("yaw")
}

internal object WorldSpawns : UUIDTable("world_spawns")

internal object Warps : UUIDTable("warps") {
    val name = varchar("name", 255).uniqueIndex()
}

internal object Homes : UUIDTable("homes") {
    val user = reference("user", OfflineUsers, CASCADE, CASCADE)
    val name = varchar("name", 255)

    init { uniqueIndex(user, name) }
}

internal object Kits : UUIDTable("kits") {
    val name = varchar("name", 255).uniqueIndex()
    val cost = decimal("cost", 100000, 10).default(BigDecimal.ZERO)
    val cooldown = long("cooldown").default(0)
}

internal object KitItems : CompositeIdTable("kit_items") {
    val kit = reference("kit", Kits, CASCADE, CASCADE)
    val slot = integer("slot").entityId()
    val material = enumeration<Material>("material")
    val data = integer("data")
    val amount = integer("amount")

    override val primaryKey = PrimaryKey(kit, slot)
}

internal object Mails : UUIDTable("mail") {
    val sender = reference("sender", OfflineUsers, CASCADE, CASCADE)
    val recipient = reference("recipient", OfflineUsers, CASCADE, CASCADE)
    val message = text("message")
}

internal object Ignores : CompositeIdTable("ignores") {
    val user = reference("user", OfflineUsers, CASCADE, CASCADE)
    val target = reference("target", OfflineUsers, CASCADE, CASCADE)

    override val primaryKey = PrimaryKey(user, target)
}