package me.zavdav.zcore.util

import java.net.Inet4Address
import java.net.InetAddress

internal fun parseInetAddress(address: String): Inet4Address {
    val parts = address.split(".")
    if (parts.size != 4) {
        throw IllegalArgumentException("Invalid address string: $address")
    }

    for (part in parts) {
        val numVal = part.toIntOrNull()
        if (numVal == null || numVal !in 0..255) {
            throw IllegalArgumentException("Invalid address string: $address")
        }
    }

    return InetAddress.getByName(address) as Inet4Address
}