package me.zavdav.zcore.punishment

/** Represents a list of punishment entries with targets of type [T]. */
sealed class PunishmentList<E : PunishmentEntry<T>, T> {

    protected val _entries = mutableListOf<E>()

    /** All entries in the list. */
    val entries: List<E> get() = _entries

    protected fun remove(target: T) {
        val entry = getLast(target) ?: return
        _entries.remove(entry)
    }

    protected fun pardon(target: T): Boolean {
        val entry = getLast(target) ?: return false
        if (!entry.active) return false
        entry.active = false
        return true
    }

    protected fun getActive(target: T): E? =
        _entries.findLast { it.active && it.target == target }

    protected fun getLast(target: T): E? =
        _entries.findLast { it.target == target }

}