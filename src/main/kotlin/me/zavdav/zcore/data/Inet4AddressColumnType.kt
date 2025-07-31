package me.zavdav.zcore.data

import me.zavdav.zcore.util.parseInetAddress
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import java.net.Inet4Address

internal class Inet4AddressColumnType : ColumnType<Inet4Address>() {

    override fun sqlType(): String = "VARCHAR(15)"

    override fun valueFromDB(value: Any): Inet4Address? = when (value) {
        is Inet4Address -> value
        is String -> parseInetAddress(value)
        else -> error("Unexpected value of type Inet4Address: $value of ${value::class.qualifiedName}")
    }

    override fun notNullValueToDB(value: Inet4Address): Any = value.hostAddress

}

internal fun Table.inet4Address(name: String) = registerColumn(name, Inet4AddressColumnType())