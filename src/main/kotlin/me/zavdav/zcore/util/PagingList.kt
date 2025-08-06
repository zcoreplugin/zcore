package me.zavdav.zcore.util

import kotlin.math.ceil
import kotlin.math.min

class PagingList<E>(val list: List<E>, val pageSize: Int): List<E> by list {

    fun page(index: Int): List<E> {
        val start = index * pageSize
        val end = min(start + pageSize, list.size)
        require(start in list.indices) { "Index out of range: $index" }

        return list.subList(start, end)
    }

    fun pages(): Int = ceil(list.size.toDouble() / pageSize).toInt()
}