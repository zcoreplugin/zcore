package me.zavdav.zcore.data

import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import java.net.Inet4Address
import java.net.InetAddress

internal class IpAddressColumnType : ColumnType<Inet4Address>() {

    override fun sqlType(): String = "VARCHAR(15)"

    override fun valueFromDB(value: Any): Inet4Address? = when (value) {
        is Inet4Address -> value
        is String -> InetAddress.getByName(value) as Inet4Address
        else -> error("Unexpected value of type Inet4Address: $value of ${value::class.qualifiedName}")
    }

    override fun notNullValueToDB(value: Inet4Address): Any = value.hostAddress

}

internal fun Table.ipAddress(name: String) = registerColumn(name, IpAddressColumnType())