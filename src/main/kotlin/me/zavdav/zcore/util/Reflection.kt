package me.zavdav.zcore.util

@Suppress("UNCHECKED_CAST")
internal fun <V> getField(obj: Any, name: String): V {
    return getField(obj, obj::class.java, name)
}

@Suppress("UNCHECKED_CAST")
internal fun <C, V> getField(obj: C, clazz: Class<out C>, name: String): V {
    val field = clazz.getDeclaredField(name)
    field.isAccessible = true
    val ret = field.get(obj) as V
    field.isAccessible = false
    return ret
}