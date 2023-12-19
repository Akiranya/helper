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

import java.util.*

/**
 * A repository of profiles, which can get or lookup [KProfile] instances
 * for given unique ids or names.
 *
 * Methods which are prefixed with **get** perform a quick local search,
 * and return no result if no value is cached locally.
 *
 * Methods which are prefixed with **lookup** perform a more complete
 * search, usually querying an underlying database.
 */
interface KProfileRepository {
    /**
     * Gets a profile from this repository, using the unique id as the base for
     * the request.
     *
     * If this repository does not contain a profile matching the unique id, a
     * profile will still be returned, but will not be populated with a name.
     *
     * @param uniqueId the unique id to get a profile for
     * @return a profile for the uuid
     */
    fun getProfile(uniqueId: UUID): KProfile

    /**
     * Gets a profile from this repository, using the name as the base for the
     * request.
     *
     * If this repository does not contain a profile matching the name, a null
     * will be returned.
     *
     * In the case that there is more than one profile in the repository
     * matching the name, the most up-to-date record is returned.
     *
     * @param name the name to get a profile for
     * @return a profile for the name
     */
    fun getProfile(name: String): KProfile?

    /**
     * Gets a collection of profiles known to the repository.
     *
     * Returned profiles will always be populated with both a unique id and a
     * username.
     *
     * @return a collection of known profiles
     */
    val knownKProfiles: Collection<KProfile>

    /**
     * Populates a map of unique id to profile for the given iterable of unique
     * ids.
     *
     * The map will only contain an entry for each given unique id if there is
     * a corresponding profile for the unique id in the repository.
     *
     * @param uniqueIds the unique ids to get profiles for
     * @return a map of uuid to profile, where possible, for each uuid in the
     *     iterable
     * @see getProfile
     */
    fun getProfiles(uniqueIds: Iterable<UUID>): Map<UUID, KProfile> {
        val ret: MutableMap<UUID, KProfile> = hashMapOf()
        for (uniqueId in uniqueIds) {
            val profile = getProfile(uniqueId)
            profile.name?.let {
                ret[uniqueId] = profile
            }
        }
        return ret
    }

    /**
     * Populates a map of name to profile for the given iterable of names.
     *
     * The map will only contain an entry for each given name if there is a
     * corresponding profile for the name in the repository.
     *
     * @param names the names to get profiles for
     * @return a map of name to profile, where possible, for each name in the
     *     iterable
     * @see getProfile
     */
    fun getProfilesByName(names: Iterable<String>): Map<String, KProfile> {
        val ret: MutableMap<String, KProfile> = hashMapOf()
        for (name in names) {
            getProfile(name)?.let { profile -> ret[name] = profile }
        }
        return ret
    }

    /**
     * Gets a profile from this repository, using the unique id as the base for
     * the request.
     *
     * If this repository does not contain a profile matching the unique id, a
     * profile will still be returned, but will not be populated with a name.
     *
     * @param uniqueId the unique id to get a profile for
     * @return a profile for the uuid
     */
    suspend fun lookupProfile(uniqueId: UUID): KProfile

    /**
     * Gets a profile from this repository, using the name as the base for the
     * request.
     *
     * If this repository does not contain a profile matching the name, a null
     * will be returned.
     *
     * In the case that there is more than one profile in the repository
     * matching the name, the most up-to-date record is returned.
     *
     * @param name the name to get a profile for
     * @return a profile for the name
     */
    suspend fun lookupProfile(name: String): KProfile?

    /**
     * Gets a collection of profiles known to the repository.
     *
     * Returned profiles will always be populated with both a unique id and a
     * username.
     *
     * @return a collection of known profiles
     */
    suspend fun lookupKnownProfiles(): Collection<KProfile>

    /**
     * Populates a map of unique id to profile for the given iterable of unique
     * ids.
     *
     * The map will only contain an entry for each given unique id if there is
     * a corresponding profile for the unique id in the repository.
     *
     * @param uniqueIds the unique ids to get profiles for
     * @return a map of uuid to profile, where possible, for each uuid in the
     *     iterable
     * @see getProfile
     */
    suspend fun lookupProfiles(uniqueIds: Iterable<UUID>): Map<UUID, KProfile>

    /**
     * Populates a map of name to profile for the given iterable of names.
     *
     * The map will only contain an entry for each given name if there is a
     * corresponding profile for the name in the repository.
     *
     * @param names the names to get profiles for
     * @return a map of name to profile, where possible, for each name in the
     *     iterable
     * @see .getProfile
     */
    suspend fun lookupProfilesByName(names: Iterable<String>): Map<String, KProfile>
}
