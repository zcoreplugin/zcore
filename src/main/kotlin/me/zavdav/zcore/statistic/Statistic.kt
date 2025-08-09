package me.zavdav.zcore.statistic

import me.zavdav.zcore.player.OfflinePlayer
import java.math.BigDecimal
import java.math.RoundingMode

/** Represents a statistic that can be tracked for every player. */
class Statistic<V : Comparable<V>> private constructor(
    val name: String,
    private val getFunction: (OfflinePlayer) -> V,
    private val formatFunction: (V) -> String
) {

    /**
     * Gets a player's score for this statistic.
     *
     * @param player the player
     * @return the player's score
     */
    fun getScore(player: OfflinePlayer): V = getFunction(player)

    /**
     * Gets the formatted representation of a player's score for this statistic.
     *
     * @param player the player
     * @return the player's score formatted
     */
    fun getFormattedScore(player: OfflinePlayer): String = formatFunction(getScore(player))

    companion object {
        private val statistics = mutableListOf<Statistic<*>>()

        /**
         * Gets all registered statistics.
         *
         * @return a list of all statistics
         */
        fun getAllRegistered(): List<Statistic<*>> = statistics.toList()

        /**
         * Gets a statistic by its name.
         *
         * @param name the statistic's name
         * @return the statistic, or `null` if it was not found
         */
        fun getByName(name: String): Statistic<*>? =
            statistics.firstOrNull { it.name.equals(name, true) }

        /**
         * Gets a statistic by its name and type.
         *
         * @param name the statistic's name
         * @param V the statistic's type
         * @return the statistic, or `null` if it was not found or the type does not match
         */
        @Suppress("UNCHECKED_CAST")
        fun <V : Comparable<V>> getTypedByName(name: String): Statistic<V>? =
            getByName(name) as? Statistic<V>

        /**
         * Registers a new statistic.
         *
         * @param name the statistic's name
         * @param getFunction function to retrieve the score for a player
         * @param formatFunction function to format scores for this statistic
         * @return `true` if the statistic was registered, `false` if another statistic
         *         with the same name is already registered (case-insensitive)
         * @throws IllegalArgumentException if the name is malformed
         */
        fun <V : Comparable<V>> register(
            name: String,
            getFunction: (OfflinePlayer) -> V,
            formatFunction: (V) -> String = { it.toString() }
        ): Boolean {
            require(name.matches(Regex("[a-zA-Z0-9_-]+"))) { "Illegal statistic name: $name" }
            val index = statistics.indexOfFirst { it.name.equals(name, true) }
            if (index != -1) return false
            statistics.add(Statistic(name, getFunction, formatFunction))
            return true
        }

        /**
         * Unregisters a statistic.
         *
         * @param name the statistic's name
         * @return `true` if the statistic was unregistered, `false` if it was not found
         */
        fun unregister(name: String): Boolean {
            val index = statistics.indexOfFirst { it.name.equals(name, true) }
            if (index == -1) return false
            statistics.removeAt(index)
            return true
        }

        internal fun registerDefaults() {
            register("playtime", { it.playtime }, { "${it.toBigDecimal().divide(BigDecimal("3600000"), 1, RoundingMode.DOWN)}h" })
            register("blocks_placed", { it.blocksPlaced })
            register("blocks_broken", { it.blocksBroken })
            register("blocks_traveled", { it.blocksTraveled }, { it.setScale(0, RoundingMode.DOWN).toString() })
            register("damage_dealt", { it.damageDealt })
            register("damage_taken", { it.damageTaken })
            register("players_killed", { it.playersKilled })
            register("mobs_killed", { it.mobsKilled })
            register("deaths", { it.deaths })
        }

        internal fun unregisterDefaults() {
            unregister("playtime")
            unregister("blocks_placed")
            unregister("blocks_broken")
            unregister("blocks_traveled")
            unregister("damage_dealt")
            unregister("damage_taken")
            unregister("players_killed")
            unregister("mobs_killed")
            unregister("deaths")
        }
    }

}