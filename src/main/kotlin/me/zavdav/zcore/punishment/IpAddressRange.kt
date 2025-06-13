package me.zavdav.zcore.punishment

import java.net.Inet4Address

/** Represents a range of IP addresses which can have many matches or exactly one match. */
class IpAddressRange private constructor(
    val component1: Int,
    val component2: Int?,
    val component3: Int?,
    val component4: Int?
) {

    init {
        require(
            component1 in 0..255 &&
            (component2 == null || component2 in 0..255) &&
            (component3 == null || component3 in 0..255) &&
            (component4 == null || component4 in 0..255)
        ) {
            "Address range components are out of range: ${toString()}"
        }
    }

    override fun toString(): String =
        "$component1.${component2 ?: "*"}.${component3 ?: "*"}.${component4 ?: "*"}"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is IpAddressRange) return false
        return component1 == other.component1 &&
               component2 == other.component2 &&
               component3 == other.component3 &&
               component4 == other.component4
    }

    override fun hashCode(): Int {
        var result = component1
        result = 31 * result + (component2 ?: 0)
        result = 31 * result + (component3 ?: 0)
        result = 31 * result + (component4 ?: 0)
        return result
    }

    /**
     * Checks if an address is in this address range.
     *
     * @param address the address
     * @return `true` if the address is in this address range
     */
    operator fun contains(address: Inet4Address): Boolean {
        val components = address.address.map { it.toInt() and 0xFF }

        return components[0] == component1 &&
               (component2 == null || components[1] == component2) &&
               (component3 == null || components[2] == component3) &&
               (component4 == null || components[3] == component4)
    }

    operator fun component1(): Int = component1

    operator fun component2(): Int? = component2

    operator fun component3(): Int? = component3

    operator fun component4(): Int? = component4

    companion object {

        /**
         * Creates an address range in the format `X.X.X.X`.
         *
         * This range will have exactly one match.
         *
         * @param component1
         * @param component2
         * @param component3
         * @param component4
         * @return the created [IpAddressRange]
         */
        fun of(component1: Int, component2: Int, component3: Int, component4: Int): IpAddressRange =
            IpAddressRange(component1, component2, component3, component4)

        /**
         * Creates an address range in the format `X.X.X.*`.
         *
         * This range will have 256 matches.
         *
         * @param component1
         * @param component2
         * @param component3
         * @return the created [IpAddressRange]
         */
        fun of(component1: Int, component2: Int, component3: Int): IpAddressRange =
            IpAddressRange(component1, component2, component3, null)

        /**
         * Creates an address range in the format `X.X.*.*`.
         *
         * This range will have 65536 matches.
         *
         * @param component1
         * @param component2
         * @return the created [IpAddressRange]
         */
        fun of(component1: Int, component2: Int): IpAddressRange =
            IpAddressRange(component1, component2, null ,null)

        /**
         * Creates an address range in the format `X.*.*.*`.
         *
         * This range will have 16777216 matches.
         *
         * @param component1
         * @return the created [IpAddressRange]
         */
        fun of(component1: Int): IpAddressRange =
            IpAddressRange(component1, null, null, null)

        /**
         * Creates an address range from an input string.
         *
         * Note that all components after a wildcard must be wildcards as well.
         *
         * @param addressString the input string
         * @return the created [IpAddressRange]
         */
        fun parse(addressString: String): IpAddressRange {
            val strings = addressString.split(".")
            if (strings.size != 4) throw IllegalArgumentException()

            val components = arrayOfNulls<Int?>(4)
            var wildcard = false

            strings.forEachIndexed { i, component ->
                val number = component.toIntOrNull()
                if (number != null) {
                    if (wildcard) throw IllegalArgumentException()
                    components[i] = number
                } else {
                    if (i == 0 || component != "*") throw IllegalArgumentException()
                    wildcard = true
                }
            }

            return IpAddressRange(components[0]!!, components[1], components[2], components[3])
        }

    }

}