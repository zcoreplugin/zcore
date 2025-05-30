package me.zavdav.zcore.punishment

/** Represents a record of punishments. */
sealed interface PunishmentList<E : PunishmentEntry<T>, T> {

    /** All entries in this list. */
    val entries: Iterable<E>

}