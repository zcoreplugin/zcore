package me.zavdav.zcore.util

@Suppress("UNCHECKED_CAST")
internal fun <V> getField(obj: Any, name: String): V {
    val field = obj::class.java.getDeclaredField(name)
    field.isAccessible = true
    val ret = field.get(obj) as V
    field.isAccessible = false
    return ret
}