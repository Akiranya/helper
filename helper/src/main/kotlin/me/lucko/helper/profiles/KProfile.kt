/*
 * This file is part of helper, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package me.lucko.helper.profiles

import org.bukkit.entity.HumanEntity
import java.util.*

/** Represents a Player's profile */
interface KProfile {
    companion object Factory {
        /**
         * Creates a new profile instance
         *
         * @param uniqueId the unique id
         * @param name the username
         * @return the profile
         */
        fun create(uniqueId: UUID, name: String?): KProfile {
            return KSimpleProfile(uniqueId, name)
        }

        /**
         * Creates a new profile instance
         *
         * @param player the player to create a profile for
         * @return the profile
         */
        fun create(player: HumanEntity): KProfile {
            return KSimpleProfile(player.uniqueId, player.name)
        }
    }

    /** Gets the unique id associated with this profile */
    val uniqueId: UUID

    /** Gets the username associated with this profile */
    val name: String?

    /**
     * Gets the timestamp when this KProfile was created or last updated.
     *
     * The returned value is a unix timestamp in milliseconds.
     */
    val timestamp: Long
}
