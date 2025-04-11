package me.zavdav.zcore.util

import java.util.EnumMap

internal fun <E> MutableList<E>.checkAndAdd(element: E): Boolean =
    if (element in this) false else add(element)

internal fun <E> MutableList<E>.checkAndRemove(element: E): Boolean =
    remove(element)

internal fun <K, V> MutableMap<K, V>.checkAndPut(key: K, value: V): Boolean =
    putIfAbsent(key, value) == null

internal fun <K, V> MutableMap<K, V>.checkAndRemove(key: K): Boolean =
    remove(key) != null

internal inline fun <reified K : Enum<K>, V> enumMap() = EnumMap<K, V>(K::class.java)