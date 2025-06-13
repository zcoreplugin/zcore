package me.zavdav.zcore.data

import me.zavdav.zcore.punishment.IpAddressRange
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table

internal class IpAddressRangeColumnType : ColumnType<IpAddressRange>() {

    override fun sqlType(): String = "VARCHAR(15)"

    override fun valueFromDB(value: Any): IpAddressRange? = when (value) {
        is IpAddressRange -> value
        is String -> IpAddressRange.parse(value)
        else -> error("Unexpected value of type IpAddressRange: $value of ${value::class.qualifiedName}")
    }

    override fun notNullValueToDB(value: IpAddressRange): Any = value.toString()

}

internal fun Table.ipAddressRange(name: String) = registerColumn(name, IpAddressRangeColumnType())