package me.lucko.helper.utils

import java.util.*

/** Utilities for converting [UUID] string representations without dashes. */
object KUndashedUuids {
    /**
     * Returns a [UUID.toString] string without dashes.
     *
     * @param uuid the uuid
     * @return the string form
     */
    fun toString(uuid: UUID): String {
        // copied from UUID impl
        return (digits(uuid.mostSignificantBits shr 32, 8) +
                digits(uuid.mostSignificantBits shr 16, 4) +
                digits(uuid.mostSignificantBits, 4) +
                digits(uuid.leastSignificantBits shr 48, 4) +
                digits(uuid.leastSignificantBits, 12))
    }

    private fun digits(value: Long, digits: Int): String {
        val hi = 1L shl digits * 4
        return java.lang.Long.toHexString(hi or (value and hi - 1)).substring(1)
    }

    /**
     * Parses a UUID from an undashed string.
     *
     * @param string the string
     * @return the uuid
     */
    fun fromString(string: String): UUID {
        require(string.length == 32) { "Invalid length " + string.length + ": " + string }
        return try {
            UUID(
                java.lang.Long.parseUnsignedLong(string.substring(0, 16), 16),
                java.lang.Long.parseUnsignedLong(string.substring(16), 16)
            )
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("Invalid uuid string: $string", e)
        }
    }
}