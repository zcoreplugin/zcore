package me.zavdav.zcore.economy

import me.zavdav.zcore.user.OfflineUser
import java.math.BigDecimal

/** Represents a user's personal economy account. */
class UserAccount(
    owner: OfflineUser,
    balance: BigDecimal = BigDecimal.ZERO,
    overdrawLimit: BigDecimal = BigDecimal.ZERO
) : EconomyAccount(owner, balance, overdrawLimit)