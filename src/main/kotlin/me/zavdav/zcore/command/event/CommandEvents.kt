package me.zavdav.zcore.command.event

import me.zavdav.zcore.economy.BankAccount
import me.zavdav.zcore.location.Home
import me.zavdav.zcore.location.Warp
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.player.TeleportRequest
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.math.BigDecimal
import java.net.Inet4Address

class BankCreateEvent(source: Player, val bankName: String) : CancellableCommandEvent<Player>(source)

class BankDeleteEvent(source: CommandSender, val bank: BankAccount) : CancellableCommandEvent<CommandSender>(source)

class BankDepositEvent(source: Player, val amount: BigDecimal, val bank: BankAccount) : CancellableCommandEvent<Player>(source)

class BankWithdrawEvent(source: Player, val amount: BigDecimal, val bank: BankAccount) : CancellableCommandEvent<Player>(source)

class PlayerPayEvent(source: Player, val target: OfflinePlayer, val amount: BigDecimal) : CancellableCommandEvent<Player>(source)

class HomeSetEvent(source: Player, val player: OfflinePlayer, val homeName: String) : CancellableCommandEvent<Player>(source)

class HomeDeleteEvent(source: CommandSender, val home: Home) : CancellableCommandEvent<CommandSender>(source)

class HomeRenameEvent(source: CommandSender, val home: Home, val newName: String) : CancellableCommandEvent<CommandSender>(source)

class HomeMoveEvent(source: Player, val home: Home, val newLocation: Location) : CancellableCommandEvent<Player>(source)

class HomeTeleportEvent(source: Player, val home: Home) : CancellableCommandEvent<Player>(source)

class WarpSetEvent(source: Player, val warpName: String) : CancellableCommandEvent<Player>(source)

class WarpDeleteEvent(source: CommandSender, val warp: Warp) : CancellableCommandEvent<CommandSender>(source)

class WarpTeleportEvent(source: Player, val warp: Warp) : CancellableCommandEvent<Player>(source)

class MessageSendEvent(source: Player, val target: Player, val message: String) : CancellableCommandEvent<Player>(source)

class MailSendEvent(source: Player, val target: OfflinePlayer, val message: String) : CancellableCommandEvent<Player>(source)

class TeleportRequestEvent(source: Player, val target: Player, val request: TeleportRequest) : CancellableCommandEvent<Player>(source)

class TeleportAcceptEvent(source: Player, val request: TeleportRequest) : CancellableCommandEvent<Player>(source)

class TeleportDenyEvent(source: Player, val request: TeleportRequest) : CancellableCommandEvent<Player>(source)

class PowerToolSetEvent(source: Player, val item: ItemStack, val command: String) : CancellableCommandEvent<Player>(source)

class PowerToolDeleteEvent(source: Player, val item: ItemStack) : CancellableCommandEvent<Player>(source)

class PlayerNickEvent(source: CommandSender, val player: OfflinePlayer, val nickname: String) : CancellableCommandEvent<CommandSender>(source)

class PlayerUnnickEvent(source: CommandSender, val player: OfflinePlayer) : CancellableCommandEvent<CommandSender>(source)

class PlayerIgnoreEvent(source: Player, val target: OfflinePlayer) : CancellableCommandEvent<Player>(source)

class PlayerUnignoreEvent(source: Player, val target: OfflinePlayer) : CancellableCommandEvent<Player>(source)

class InvincibilityEnableEvent(source: CommandSender, val player: Player) : CancellableCommandEvent<CommandSender>(source)

class InvincibilityDisableEvent(source: CommandSender, val player: Player) : CancellableCommandEvent<CommandSender>(source)

class VanishEnableEvent(source: CommandSender, val player: Player) : CancellableCommandEvent<CommandSender>(source)

class VanishDisableEvent(source: CommandSender, val player: Player) : CancellableCommandEvent<CommandSender>(source)

class SocialSpyEnableEvent(source: CommandSender, val player: Player) : CancellableCommandEvent<CommandSender>(source)

class SocialSpyDisableEvent(source: CommandSender, val player: Player) : CancellableCommandEvent<CommandSender>(source)

class PlayerBanEvent(source: CommandSender, val player: OfflinePlayer, val duration: Long?, val reason: String) : CancellableCommandEvent<CommandSender>(source)

class PlayerUnbanEvent(source: CommandSender, val player: OfflinePlayer) : CancellableCommandEvent<CommandSender>(source)

class IpBanEvent(source: CommandSender, val address: Inet4Address, val duration: Long?, val reason: String) : CancellableCommandEvent<CommandSender>(source)

class IpUnbanEvent(source: CommandSender, val address: Inet4Address) : CancellableCommandEvent<CommandSender>(source)

class PlayerMuteEvent(source: CommandSender, val player: OfflinePlayer, val duration: Long?, val reason: String) : CancellableCommandEvent<CommandSender>(source)

class PlayerUnmuteEvent(source: CommandSender, val player: OfflinePlayer) : CancellableCommandEvent<CommandSender>(source)