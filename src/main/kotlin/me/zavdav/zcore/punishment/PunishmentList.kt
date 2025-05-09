package me.zavdav.zcore.punishment

/** Represents a record of punishments. */
sealed class PunishmentList<E : PunishmentEntry<T>, T> {

    /** All entries in this list. */
    abstract val entries: Iterable<E>

}