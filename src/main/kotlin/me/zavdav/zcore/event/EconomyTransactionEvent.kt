package me.zavdav.zcore.event

import me.zavdav.zcore.economy.EconomyAccount
import org.bukkit.event.Event
import java.math.BigDecimal

/** Called when a transaction between two economy accounts occurs. */
class EconomyTransactionEvent(

    /** The account that sent the money. */
    val source: EconomyAccount,

    /** The account that received the money. */
    val target: EconomyAccount,

    /** The amount of money that was sent. */
    val amount: BigDecimal

) : Event("EconomyTransactionEvent")