package me.zavdav.zcore.version

import me.zavdav.zcore.ZCore

/** Represents a version of ZCore in the SemVer format. */
class ZCoreVersion(
    val major: Int,
    val minor: Int,
    val patch: Int
) : Comparable<ZCoreVersion> {

    private val version: Int = versionOf(major, minor, patch)

    private fun versionOf(major: Int, minor: Int, patch: Int): Int {
        require(major in 0..MAX_COMPONENT_VALUE &&
                minor in 0..MAX_COMPONENT_VALUE &&
                patch in 0..MAX_COMPONENT_VALUE) {
            "Version components are out of range: $major.$minor.$patch"
        }
        return major.shl(16) + minor.shl(8) + patch
    }

    override fun toString(): String = "$major.$minor.$patch"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ZCoreVersion) return false
        return version == other.version
    }

    override fun hashCode(): Int = version

    override fun compareTo(other: ZCoreVersion): Int = version - other.version

    companion object {
        private const val MAX_COMPONENT_VALUE: Int = 255
        private val versionRegex = Regex("""^(\d+)\.(\d+)\.(\d+)""")

        val CURRENT by lazy { parse(ZCore.INSTANCE.description.version) }

        fun parse(versionString: String): ZCoreVersion {
            val result = versionRegex.find(versionString)
            require(result != null) { "Invalid version string: $versionString" }
            val (major, minor, patch) = result.destructured
            return ZCoreVersion(major.toInt(), minor.toInt(), patch.toInt())
        }
    }

}