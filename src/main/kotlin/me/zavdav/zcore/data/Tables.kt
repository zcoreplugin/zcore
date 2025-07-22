package me.zavdav.zcore.data

import org.bukkit.Material
import org.jetbrains.exposed.dao.id.CompositeIdTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption.CASCADE
import java.math.BigDecimal

internal object OfflinePlayers : UUIDTable("offline_players") {
    val name = varchar("name", 16).uniqueIndex()
    val nickname = varchar("nickname", 255).nullable().default(null)
    val firstJoin = long("first_join")
    val lastJoin = long("last_join")
    val lastActivity = long("last_activity")
    val isInvincible = bool("is_invincible").default(false)
    val isVanished = bool("is_vanished").default(false)
    val isSocialSpy = bool("is_socialspy").default(false)
    val playtime = long("playtime").default(0)
    val blocksPlaced = long("blocks_placed").default(0)
    val blocksBroken = long("blocks_broken").default(0)
    val blocksTraveled = decimal("blocks_traveled", 100000, 10).default(BigDecimal.ZERO)
    val damageDealt = long("damage_dealt").default(0)
    val damageTaken = long("damage_taken").default(0)
    val playersKilled = long("players_killed").default(0)
    val mobsKilled = long("mobs_killed").default(0)
    val deaths = long("deaths").default(0)
}

internal object PersonalAccounts : UUIDTable("personal_accounts") {
    val owner = reference("owner", OfflinePlayers, CASCADE, CASCADE).uniqueIndex()
    val balance = decimal("balance", 100000, 10).default(BigDecimal.ZERO)
    val overdrawLimit = decimal("overdraw_limit", 100000, 10).default(BigDecimal.ZERO)
}

internal object BankAccounts : UUIDTable("bank_accounts") {
    val name = varchar("name", 255).uniqueIndex()
    val owner = reference("owner", OfflinePlayers, CASCADE, CASCADE)
    val balance = decimal("balance", 100000, 10).default(BigDecimal.ZERO)
    val overdrawLimit = decimal("overdraw_limit", 100000, 10).default(BigDecimal.ZERO)
}

internal object BankMembers : CompositeIdTable("bank_members") {
    val bank = reference("bank", BankAccounts, CASCADE, CASCADE)
    val player = reference("player", OfflinePlayers, CASCADE, CASCADE)

    override val primaryKey = PrimaryKey(bank, player)
}

internal object Mutes : UUIDTable("mutes") {
    val target = reference("target", OfflinePlayers, CASCADE, CASCADE)
    val issuer = reference("issuer", OfflinePlayers, CASCADE, CASCADE).nullable()
    val timeIssued = long("time_issued")
    val duration = long("duration").nullable()
    val reason = text("reason")
    val pardoned = bool("pardoned").default(false)
}

internal object Bans : UUIDTable("bans") {
    val target = reference("target", OfflinePlayers, CASCADE, CASCADE)
    val issuer = reference("issuer", OfflinePlayers, CASCADE, CASCADE).nullable()
    val timeIssued = long("time_issued")
    val duration = long("duration").nullable()
    val reason = text("reason")
    val pardoned = bool("pardoned").default(false)
}

internal object IpBans : UUIDTable("ip_bans") {
    val target = ipAddressRange("target")
    val issuer = reference("issuer", OfflinePlayers, CASCADE, CASCADE).nullable()
    val timeIssued = long("time_issued")
    val duration = long("duration").nullable()
    val reason = text("reason")
    val pardoned = bool("pardoned").default(false)
}

internal object Homes : UUIDTable("homes") {
    val player = reference("player", OfflinePlayers, CASCADE, CASCADE)
    val name = varchar("name", 255)
    val world = varchar("world", 255)
    val x = double("x")
    val y = double("y")
    val z = double("z")
    val pitch = float("pitch")
    val yaw = float("yaw")

    init { uniqueIndex(player, name) }
}

internal object Warps : UUIDTable("warps") {
    val name = varchar("name", 255).uniqueIndex()
    val world = varchar("world", 255)
    val x = double("x")
    val y = double("y")
    val z = double("z")
    val pitch = float("pitch")
    val yaw = float("yaw")
}

internal object Kits : UUIDTable("kits") {
    val name = varchar("name", 255).uniqueIndex()
    val cost = decimal("cost", 100000, 10).default(BigDecimal.ZERO)
    val cooldown = long("cooldown").default(0)
}

internal object KitItems : UUIDTable("kit_items") {
    val kit = reference("kit", Kits, CASCADE, CASCADE)
    val slot = integer("slot")
    val material = enumeration<Material>("material")
    val data = short("data")
    val amount = integer("amount")

    init { uniqueIndex(kit, slot) }
}

internal object Mails : UUIDTable("mails") {
    val sender = reference("sender", OfflinePlayers, CASCADE, CASCADE)
    val recipient = reference("recipient", OfflinePlayers, CASCADE, CASCADE)
    val message = text("message")
}

internal object Ignores : CompositeIdTable("ignores") {
    val player = reference("player", OfflinePlayers, CASCADE, CASCADE)
    val target = reference("target", OfflinePlayers, CASCADE, CASCADE)

    override val primaryKey = PrimaryKey(player, target)
}