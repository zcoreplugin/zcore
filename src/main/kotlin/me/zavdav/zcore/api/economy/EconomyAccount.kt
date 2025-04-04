package me.zavdav.zcore.api.economy

import java.math.BigDecimal

/** Represents an economy account with an owner, specified by the type [O]. */
interface EconomyAccount<O> {

    /** The account's owner. */
    var owner: O

    /** The account's current balance. */
    var balance: BigDecimal

    /** Determines if the balance can be negative. */
    var canBeNegative: Boolean

}