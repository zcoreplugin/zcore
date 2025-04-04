package me.zavdav.zcore.api.punishments

/** Represents a list of punishments which contains entries of type [E]. */
interface PunishmentList<E : PunishmentEntry<T>, T> : Iterable<E> {

    /** A [Set] of all entries in the list. */
    val entries: Set<E>

    /** Adds an [entry] to the punishment list. */
    fun add(entry: E): Boolean

    /** Removes an [entry] from the punishment list. */
    fun remove(entry: E): Boolean

    /** Pardons a punishment without removing it from the list. */
    fun pardon(entry: E): Boolean

    /** Determines if the list contains an [entry]. */
    operator fun contains(entry: E): Boolean

    /** Gets all entries with the specified [target]. */
    fun getByTarget(target: T): Set<E>

}