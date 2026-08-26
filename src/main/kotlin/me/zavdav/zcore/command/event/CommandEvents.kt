package me.zavdav.zcore.command.event

import me.zavdav.zcore.economy.BankAccount
import me.zavdav.zcore.location.Home
import me.zavdav.zcore.location.Warp
import me.zavdav.zcore.player.OfflinePlayer
import me.zavdav.zcore.player.TeleportRequest
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.inventory.ItemStack
import java.math.BigDecimal
import java.net.Inet4Address

class BankCreateEvent(source: Player, val bankName: String) : CancellableCommandEvent<Player>(source) {
    companion object {
        private val HANDLER_LIST: HandlerList = HandlerList()
            @JvmName("getHandlerList") get
    }

    override fun getHandlers(): HandlerList {
        return HANDLER_LIST
    }
}

class BankDeleteEvent(source: CommandSender, val bank: BankAccount) : CancellableCommandEvent<CommandSender>(source) {
    companion object {
        private val HANDLER_LIST: HandlerList = HandlerList()
            @JvmName("getHandlerList") get
    }

    override fun getHandlers(): HandlerList {
        return HANDLER_LIST
    }
}

class BankDepositEvent(source: Player, val amount: BigDecimal, val bank: BankAccount) : CancellableCommandEvent<Player>(source) {
    companion object {
        private val HANDLER_LIST: HandlerList = HandlerList()
            @JvmName("getHandlerList") get
    }

    override fun getHandlers(): HandlerList {
        return HANDLER_LIST
    }
}

class BankWithdrawEvent(source: Player, val amount: BigDecimal, val bank: BankAccount) : CancellableCommandEvent<Player>(source) {
    companion object {
        private val HANDLER_LIST: HandlerList = HandlerList()
            @JvmName("getHandlerList") get
    }

    override fun getHandlers(): HandlerList {
        return HANDLER_LIST
    }
}

class PlayerPayEvent(source: Player, val target: OfflinePlayer, val amount: BigDecimal) : CancellableCommandEvent<Player>(source) {
    companion object {
        private val HANDLER_LIST: HandlerList = HandlerList()
            @JvmName("getHandlerList") get
    }

    override fun getHandlers(): HandlerList {
        return HANDLER_LIST
    }
}

class HomeSetEvent(source: Player, val player: OfflinePlayer, val homeName: String) : CancellableCommandEvent<Player>(source) {
    companion object {
        private val HANDLER_LIST: HandlerList = HandlerList()
            @JvmName("getHandlerList") get
    }

    override fun getHandlers(): HandlerList {
        return HANDLER_LIST
    }
}

class HomeDeleteEvent(source: CommandSender, val home: Home) : CancellableCommandEvent<CommandSender>(source) {
    companion object {
        private val HANDLER_LIST: HandlerList = HandlerList()
            @JvmName("getHandlerList") get
    }

    override fun getHandlers(): HandlerList {
        return HANDLER_LIST
    }
}

class HomeRenameEvent(source: CommandSender, val home: Home, val newName: String) : CancellableCommandEvent<CommandSender>(source) {
    companion object {
        private val HANDLER_LIST: HandlerList = HandlerList()
            @JvmName("getHandlerList") get
    }

    override fun getHandlers(): HandlerList {
        return HANDLER_LIST
    }
}

class HomeMoveEvent(source: Player, val home: Home, val newLocation: Location) : CancellableCommandEvent<Player>(source) {
    companion object {
        private val HANDLER_LIST: HandlerList = HandlerList()
            @JvmName("getHandlerList") get
    }

    override fun getHandlers(): HandlerList {
        return HANDLER_LIST
    }
}

class HomeTeleportEvent(source: Player, val home: Home) : CancellableCommandEvent<Player>(source) {
    companion object {
        private val HANDLER_LIST: HandlerList = HandlerList()
            @JvmName("getHandlerList") get
    }

    override fun getHandlers(): HandlerList {
        return HANDLER_LIST
    }
}

class WarpSetEvent(source: Player, val warpName: String) : CancellableCommandEvent<Player>(source) {
    companion object {
        private val HANDLER_LIST: HandlerList = HandlerList()
            @JvmName("getHandlerList") get
    }

    override fun getHandlers(): HandlerList {
        return HANDLER_LIST
    }
}

class WarpDeleteEvent(source: CommandSender, val warp: Warp) : CancellableCommandEvent<CommandSender>(source) {
    companion object {
        private val HANDLER_LIST: HandlerList = HandlerList()
            @JvmName("getHandlerList") get
    }

    override fun getHandlers(): HandlerList {
        return HANDLER_LIST
    }
}

class WarpTeleportEvent(source: Player, val warp: Warp) : CancellableCommandEvent<Player>(source) {
    companion object {
        private val HANDLER_LIST: HandlerList = HandlerList()
            @JvmName("getHandlerList") get
    }

    override fun getHandlers(): HandlerList {
        return HANDLER_LIST
    }
}

class MessageSendEvent(source: Player, val target: Player, val message: String) : CancellableCommandEvent<Player>(source) {
    companion object {
        private val HANDLER_LIST: HandlerList = HandlerList()
            @JvmName("getHandlerList") get
    }

    override fun getHandlers(): HandlerList {
        return HANDLER_LIST
    }
}

class MailSendEvent(source: Player, val target: OfflinePlayer, val message: String) : CancellableCommandEvent<Player>(source) {
    companion object {
        private val HANDLER_LIST: HandlerList = HandlerList()
            @JvmName("getHandlerList") get
    }

    override fun getHandlers(): HandlerList {
        return HANDLER_LIST
    }
}

class TeleportRequestEvent(source: Player, val target: Player, val request: TeleportRequest) : CancellableCommandEvent<Player>(source) {
    companion object {
        private val HANDLER_LIST: HandlerList = HandlerList()
            @JvmName("getHandlerList") get
    }

    override fun getHandlers(): HandlerList {
        return HANDLER_LIST
    }
}

class TeleportAcceptEvent(source: Player, val request: TeleportRequest) : CancellableCommandEvent<Player>(source) {
    companion object {
        private val HANDLER_LIST: HandlerList = HandlerList()
            @JvmName("getHandlerList") get
    }

    override fun getHandlers(): HandlerList {
        return HANDLER_LIST
    }
}

class TeleportDenyEvent(source: Player, val request: TeleportRequest) : CancellableCommandEvent<Player>(source) {
    companion object {
        private val HANDLER_LIST: HandlerList = HandlerList()
            @JvmName("getHandlerList") get
    }

    override fun getHandlers(): HandlerList {
        return HANDLER_LIST
    }
}

class PowerToolSetEvent(source: Player, val item: ItemStack, val command: String) : CancellableCommandEvent<Player>(source) {
    companion object {
        private val HANDLER_LIST: HandlerList = HandlerList()
            @JvmName("getHandlerList") get
    }

    override fun getHandlers(): HandlerList {
        return HANDLER_LIST
    }
}

class PowerToolDeleteEvent(source: Player, val item: ItemStack) : CancellableCommandEvent<Player>(source) {
    companion object {
        private val HANDLER_LIST: HandlerList = HandlerList()
            @JvmName("getHandlerList") get
    }

    override fun getHandlers(): HandlerList {
        return HANDLER_LIST
    }
}

class PlayerNickEvent(source: CommandSender, val player: OfflinePlayer, val nickname: String) : CancellableCommandEvent<CommandSender>(source) {
    companion object {
        private val HANDLER_LIST: HandlerList = HandlerList()
            @JvmName("getHandlerList") get
    }

    override fun getHandlers(): HandlerList {
        return HANDLER_LIST
    }
}

class PlayerUnnickEvent(source: CommandSender, val player: OfflinePlayer) : CancellableCommandEvent<CommandSender>(source) {
    companion object {
        private val HANDLER_LIST: HandlerList = HandlerList()
            @JvmName("getHandlerList") get
    }

    override fun getHandlers(): HandlerList {
        return HANDLER_LIST
    }
}

class PlayerIgnoreEvent(source: Player, val target: OfflinePlayer) : CancellableCommandEvent<Player>(source) {
    companion object {
        private val HANDLER_LIST: HandlerList = HandlerList()
            @JvmName("getHandlerList") get
    }

    override fun getHandlers(): HandlerList {
        return HANDLER_LIST
    }
}

class PlayerUnignoreEvent(source: Player, val target: OfflinePlayer) : CancellableCommandEvent<Player>(source) {
    companion object {
        private val HANDLER_LIST: HandlerList = HandlerList()
            @JvmName("getHandlerList") get
    }

    override fun getHandlers(): HandlerList {
        return HANDLER_LIST
    }
}

class InvincibilityEnableEvent(source: CommandSender, val player: Player) : CancellableCommandEvent<CommandSender>(source) {
    companion object {
        private val HANDLER_LIST: HandlerList = HandlerList()
            @JvmName("getHandlerList") get
    }

    override fun getHandlers(): HandlerList {
        return HANDLER_LIST
    }
}

class InvincibilityDisableEvent(source: CommandSender, val player: Player) : CancellableCommandEvent<CommandSender>(source) {
    companion object {
        private val HANDLER_LIST: HandlerList = HandlerList()
            @JvmName("getHandlerList") get
    }

    override fun getHandlers(): HandlerList {
        return HANDLER_LIST
    }
}

class VanishEnableEvent(source: CommandSender, val player: Player) : CancellableCommandEvent<CommandSender>(source) {
    companion object {
        private val HANDLER_LIST: HandlerList = HandlerList()
            @JvmName("getHandlerList") get
    }

    override fun getHandlers(): HandlerList {
        return HANDLER_LIST
    }
}

class VanishDisableEvent(source: CommandSender, val player: Player) : CancellableCommandEvent<CommandSender>(source) {
    companion object {
        private val HANDLER_LIST: HandlerList = HandlerList()
            @JvmName("getHandlerList") get
    }

    override fun getHandlers(): HandlerList {
        return HANDLER_LIST
    }
}

class SocialSpyEnableEvent(source: CommandSender, val player: Player) : CancellableCommandEvent<CommandSender>(source) {
    companion object {
        private val HANDLER_LIST: HandlerList = HandlerList()
            @JvmName("getHandlerList") get
    }

    override fun getHandlers(): HandlerList {
        return HANDLER_LIST
    }
}

class SocialSpyDisableEvent(source: CommandSender, val player: Player) : CancellableCommandEvent<CommandSender>(source) {
    companion object {
        private val HANDLER_LIST: HandlerList = HandlerList()
            @JvmName("getHandlerList") get
    }

    override fun getHandlers(): HandlerList {
        return HANDLER_LIST
    }
}

class PlayerBanEvent(source: CommandSender, val player: OfflinePlayer, val duration: Long?, val reason: String) : CancellableCommandEvent<CommandSender>(source) {
    companion object {
        private val HANDLER_LIST: HandlerList = HandlerList()
            @JvmName("getHandlerList") get
    }

    override fun getHandlers(): HandlerList {
        return HANDLER_LIST
    }
}

class PlayerUnbanEvent(source: CommandSender, val player: OfflinePlayer) : CancellableCommandEvent<CommandSender>(source) {
    companion object {
        private val HANDLER_LIST: HandlerList = HandlerList()
            @JvmName("getHandlerList") get
    }

    override fun getHandlers(): HandlerList {
        return HANDLER_LIST
    }
}

class IpBanEvent(source: CommandSender, val address: Inet4Address, val duration: Long?, val reason: String) : CancellableCommandEvent<CommandSender>(source) {
    companion object {
        private val HANDLER_LIST: HandlerList = HandlerList()
            @JvmName("getHandlerList") get
    }

    override fun getHandlers(): HandlerList {
        return HANDLER_LIST
    }
}

class IpUnbanEvent(source: CommandSender, val address: Inet4Address) : CancellableCommandEvent<CommandSender>(source) {
    companion object {
        private val HANDLER_LIST: HandlerList = HandlerList()
            @JvmName("getHandlerList") get
    }

    override fun getHandlers(): HandlerList {
        return HANDLER_LIST
    }
}

class PlayerMuteEvent(source: CommandSender, val player: OfflinePlayer, val duration: Long?, val reason: String) : CancellableCommandEvent<CommandSender>(source) {
    companion object {
        private val HANDLER_LIST: HandlerList = HandlerList()
            @JvmName("getHandlerList") get
    }

    override fun getHandlers(): HandlerList {
        return HANDLER_LIST
    }
}

class PlayerUnmuteEvent(source: CommandSender, val player: OfflinePlayer) : CancellableCommandEvent<CommandSender>(source) {
    companion object {
        private val HANDLER_LIST: HandlerList = HandlerList()
            @JvmName("getHandlerList") get
    }

    override fun getHandlers(): HandlerList {
        return HANDLER_LIST
    }
}