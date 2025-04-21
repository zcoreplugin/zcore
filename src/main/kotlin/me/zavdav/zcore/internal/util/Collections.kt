package me.zavdav.zcore.internal.util

import java.util.EnumMap

internal fun <E> MutableList<E>.addIfAbsent(element: E): Boolean =
    if (element in this) false else add(element)

internal inline fun <reified K : Enum<K>, V> enumMap() = EnumMap<K, V>(K::class.java)