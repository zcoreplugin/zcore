package me.zavdav.zcore.internal.model

import me.zavdav.zcore.economy.BankAccount
import org.bukkit.Material
import org.bukkit.entity.CreatureType
import org.jetbrains.exposed.sql.Table

internal object OfflineUsers : Table("offline_user") {
    val uniqueId = uuid("unique_id")
    val name = varchar("name", 16).uniqueIndex()
    val nickname = varchar("nickname", 255).nullable()
    val firstJoin = long("first_join")
    val lastJoin = long("last_join")
    val lastOnline = long("last_online")
    val accountId = uuid("account_id") references UserAccounts.uniqueId
    val isInvincible = bool("is_invincible")
    val isVanished = bool("is_vanished")
    val isChatEnabled = bool("is_chat_enabled")
    val isSocialSpyEnabled = bool("is_socialspy_enabled")

    override val primaryKey = PrimaryKey(uniqueId)
}

internal object UserHomes : Table("user_home") {
    val userId = uuid("user_id") references OfflineUsers.uniqueId
    val name = varchar("name", 255)
    val locationId = uuid("location_id") references Locations.uniqueId

    override val primaryKey = PrimaryKey(userId, name)
}

internal object UserMails : Table("user_mail") {
    val uniqueId = uuid("unique_id").autoGenerate()
    val userId = uuid("user_id") references OfflineUsers.uniqueId
    val sourceUserId = uuid("source_user_id") references OfflineUsers.uniqueId
    val message = text("message")

    override val primaryKey = PrimaryKey(uniqueId)
}

internal object UserIgnoredUsers : Table("user_ignored_user") {
    val userId = uuid("user_id") references OfflineUsers.uniqueId
    val ignoredUserId = uuid("ignored_user_id") references OfflineUsers.uniqueId

    override val primaryKey = PrimaryKey(userId, ignoredUserId)
}

internal object UserAccounts : Table("user_account") {
    val uniqueId = uuid("unique_id").autoGenerate()
    val ownerId = uuid("owner_id") references OfflineUsers.uniqueId
    val balance = decimal("balance", Int.MAX_VALUE, 10)
    val overdrawLimit = decimal("overdraw_limit", Int.MAX_VALUE, 10)

    override val primaryKey = PrimaryKey(uniqueId)
}

internal object BankAccounts : Table("bank_account") {
    val uniqueId = uuid("unique_id") references UserAccounts.uniqueId
    val name = varchar("name", 255)

    override val primaryKey = PrimaryKey(uniqueId)
}

internal object BankAccountUsers : Table("bank_account_user") {
    val bankId = uuid("bank_id") references BankAccounts.uniqueId
    val userId = uuid("user_id") references OfflineUsers.uniqueId
    val role = enumeration<BankAccount.Role>("role")

    override val primaryKey = PrimaryKey(bankId, userId)
}

internal object UserStatistics : Table("user_statistics") {
    val userId = uuid("user_id") references OfflineUsers.uniqueId
    val playtime = long("playtime")
    val blocksTraveled = decimal("blocks_traveled", Int.MAX_VALUE, 10)
    val damageDealt = long("damage_dealt")
    val damageTaken = long("damage_taken")
    val deaths = long("deaths")

    override val primaryKey = PrimaryKey(userId)
}

internal object DetailsBlocksPlaced : Table("detail_blocks_placed") {
    val userId = uuid("user_id") references UserStatistics.userId
    val material = enumeration<Material>("material")
    val amount = long("amount")

    override val primaryKey = PrimaryKey(userId, material)
}

internal object DetailsBlocksBroken : Table("detail_blocks_broken") {
    val userId = uuid("user_id") references UserStatistics.userId
    val material = enumeration<Material>("material")
    val amount = long("amount")

    override val primaryKey = PrimaryKey(userId, material)
}

internal object DetailsItemsDropped : Table("detail_items_dropped") {
    val userId = uuid("user_id") references UserStatistics.userId
    val material = enumeration<Material>("material")
    val amount = long("amount")

    override val primaryKey = PrimaryKey(userId, material)
}

internal object DetailsKilledUsers : Table("detail_killed_users") {
    val userId = uuid("user_id") references UserStatistics.userId
    val killedUserId = uuid("killed_user_id") references OfflineUsers.uniqueId
    val amount = long("amount")

    override val primaryKey = PrimaryKey(userId, killedUserId)
}

internal object DetailsKilledMobs : Table("detail_killed_mobs") {
    val userId = uuid("user_id") references UserStatistics.userId
    val creature = enumeration<CreatureType>("creature")
    val amount = long("amount")

    override val primaryKey = PrimaryKey(userId, creature)
}

internal object PunishmentEntries : Table("punishment_entry") {
    val uniqueId = uuid("unique_id").autoGenerate()
    val issuerId = uuid("issuer_id") references OfflineUsers.uniqueId
    val timeIssued = long("time_issued")
    val duration = long("duration").nullable()
    val reason = text("reason")
    val active = bool("active")

    override val primaryKey = PrimaryKey(uniqueId)
}

internal object MuteEntries : Table("mute_entry") {
    val uniqueId = uuid("unique_id") references PunishmentEntries.uniqueId
    val userId = uuid("user_id") references OfflineUsers.uniqueId

    override val primaryKey = PrimaryKey(uniqueId)
}

internal object BanEntries : Table("ban_entry") {
    val uniqueId = uuid("unique_id") references PunishmentEntries.uniqueId
    val uuid = uuid("uuid")

    override val primaryKey = PrimaryKey(uniqueId)
}

internal object IpBanEntries : Table("ip_ban_entry") {
    val uniqueId = uuid("unique_id") references PunishmentEntries.uniqueId
    val ipAddress = varchar("ip_address", 15)

    override val primaryKey = PrimaryKey(uniqueId)
}

internal object IpBanCapturedUuids : Table("ip_ban_captured_uuid") {
    val ipBanId = uuid("ip_ban_id") references IpBanEntries.uniqueId
    val uuid = uuid("uuid")

    override val primaryKey = PrimaryKey(ipBanId, uuid)
}

internal object Locations : Table("location") {
    val uniqueId = uuid("unique_id").autoGenerate()
    val worldName = varchar("world_name", 255)
    val x = double("x")
    val y = double("y")
    val z = double("z")
    val pitch = float("pitch")
    val yaw = float("yaw")

    override val primaryKey = PrimaryKey(uniqueId)
}

internal object WorldSpawns : Table("world_spawn") {
    val name = varchar("name", 255)
    val locationId = uuid("location_id") references Locations.uniqueId

    override val primaryKey = PrimaryKey(name)
}

internal object Warps : Table("warp") {
    val name = varchar("name", 255)
    val locationId = uuid("location_id") references Locations.uniqueId

    override val primaryKey = PrimaryKey(name)
}

internal object Kits : Table("kit") {
    val uniqueId = uuid("unique_id").autoGenerate()
    val name = varchar("name", 255).uniqueIndex()
    val cost = decimal("cost", Int.MAX_VALUE, 10)
    val cooldown = long("cooldown")

    override val primaryKey = PrimaryKey(uniqueId)
}

internal object KitItemStacks : Table("kit_item_stack") {
    val kitId = uuid("kit_id") references Kits.uniqueId
    val slot = integer("slot")
    val material = enumeration<Material>("material")
    val data = integer("data")
    val amount = integer("amount")

    override val primaryKey = PrimaryKey(kitId, slot)
}