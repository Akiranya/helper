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

package me.lucko.helper.profiles.plugin

import com.github.benmanes.caffeine.cache.Cache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.lucko.helper.profiles.KProfileRepository
import me.lucko.helper.profiles.Profile
import me.lucko.helper.sql.Sql
import me.lucko.helper.utils.UndashedUuids.fromString
import me.lucko.helper.utils.UndashedUuids.toString
import java.sql.SQLException
import java.util.*
import kotlin.jvm.optionals.getOrNull

class KHelperProfileRepository(
    internal: HelperProfileInternal,
) : KProfileRepository {

    companion object {
        private fun isValidMcUsername(s: String): Boolean {
            return HelperProfileInternal.MINECRAFT_USERNAME_PATTERN.matcher(s).matches()
        }
    }

    private val profileMap: Cache<UUID, ImmutableProfile> = internal.profileMap
    private val sql: Sql = internal.sql
    private val tableName: String = internal.tableName

    private fun replaceTableName(s: String): String {
        return s.replace("{table}", tableName)
    }

    private fun updateCache(profile: ImmutableProfile) {
        val existing = profileMap.getIfPresent(profile.uniqueId)
        if (existing == null || existing.timestamp < profile.timestamp) {
            profileMap.put(profile.uniqueId, profile)
        }
    }

    override fun getProfile(uniqueId: UUID): Profile {
        var profile: Profile? = profileMap.getIfPresent(uniqueId)
        if (profile == null) {
            profile = ImmutableProfile(uniqueId, null, 0)
        }
        return profile
    }

    override fun getProfile(name: String): Profile? {
        for (profile in profileMap.asMap().values) {
            val profileName = profile.name.getOrNull()
            if (profileName != null && profileName.equals(name, ignoreCase = true)) {
                return profile
            }
        }
        return null
    }

    override val knownKProfiles: Collection<Profile>
        get() = Collections.unmodifiableCollection(profileMap.asMap().values)

    override suspend fun lookupProfile(uniqueId: UUID): Profile = withContext(Dispatchers.IO) io@{
        val profile = getProfile(uniqueId)
        if (profile.name.isPresent) {
            return@io profile
        } else {
            try {
                sql.connection.use { c ->
                    c.prepareStatement(replaceTableName(HelperProfileInternal.SELECT_UID)).use { ps ->
                        ps.setString(1, toString(uniqueId))
                        ps.executeQuery().use { rs ->
                            if (rs.next()) {
                                val name = rs.getString("name")
                                val lastUpdate = rs.getTimestamp("lastupdate")
                                val p = ImmutableProfile(uniqueId, name, lastUpdate.getTime())
                                updateCache(p)
                                return@io p
                            }
                        }
                    }
                }
            } catch (e: SQLException) {
                e.printStackTrace()
            }
            return@io ImmutableProfile(uniqueId, null, 0)
        }
    }

    override suspend fun lookupProfile(name: String): Profile? = withContext(Dispatchers.IO) io@{
        val profile = getProfile(name)
        if (profile != null) {
            return@io profile
        } else {
            try {
                sql.connection.use { c ->
                    c.prepareStatement(replaceTableName(HelperProfileInternal.SELECT_NAME)).use { ps ->
                        ps.setString(1, name)
                        ps.executeQuery().use { rs ->
                            if (rs.next()) {
                                val remoteName = rs.getString("name") // provide a case corrected name
                                val lastUpdate = rs.getTimestamp("lastupdate")
                                val uuidString = rs.getString("canonicalid")
                                val uuid = fromString(uuidString)
                                val p = ImmutableProfile(uuid, remoteName, lastUpdate.getTime())
                                updateCache(p)
                                return@io p
                            }
                        }
                    }
                }
            } catch (e: SQLException) {
                e.printStackTrace()
            }
            return@io null
        }
    }

    override suspend fun lookupKnownProfiles(): Collection<Profile> = withContext(Dispatchers.IO) io@{
        val ret = HashSet<Profile>()
        try {
            sql.connection.use { c ->
                c.prepareStatement(replaceTableName(HelperProfileInternal.SELECT_ALL)).use { ps ->
                    ps.executeQuery().use { rs ->
                        while (rs.next()) {
                            val name = rs.getString("name")
                            val lastUpdate = rs.getTimestamp("lastupdate")
                            val uuidString = rs.getString("canonicalid")
                            val uuid = fromString(uuidString)
                            val p = ImmutableProfile(uuid, name, lastUpdate.getTime())
                            updateCache(p)
                            ret.add(p)
                        }
                    }
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return@io ret
    }

    override suspend fun lookupProfiles(uniqueIds: Iterable<UUID>): Map<UUID, Profile> {
        val toFind = HashSet<UUID>().apply { addAll(uniqueIds) }
        val ret = HashMap<UUID, Profile>()
        val iterator = toFind.iterator()
        while (iterator.hasNext()) {
            val u = iterator.next()
            val profile = getProfile(u)
            if (profile.name.isPresent) {
                ret[u] = profile
                iterator.remove()
            }
        }
        val sb = StringBuilder("(")
        var first = true
        for (uniqueId in toFind) {
            if (!first) {
                sb.append(", ")
            }
            sb.append("UNHEX('").append(toString(uniqueId)).append("')")
            first = false
        }
        if (first) {
            return ret
        }
        sb.append(")")
        return withContext(Dispatchers.IO) {
            try {
                sql.connection.use { c ->
                    c.createStatement().use { s ->
                        s.executeQuery(replaceTableName(String.format(HelperProfileInternal.SELECT_ALL_UIDS, sb.toString()))).use { rs ->
                            while (rs.next()) {
                                val name = rs.getString("name")
                                val lastUpdate = rs.getTimestamp("lastupdate")
                                val uuidString = rs.getString("canonicalid")
                                val uuid = fromString(uuidString)
                                val p = ImmutableProfile(uuid, name, lastUpdate.getTime())
                                updateCache(p)
                                ret[uuid] = p
                            }
                        }
                    }
                }
            } catch (e: SQLException) {
                e.printStackTrace()
            }
            ret
        }
    }

    override suspend fun lookupProfilesByName(names: Iterable<String>): Map<String, Profile> {
        val toFind = HashSet<String>().apply { addAll(names) }
        val ret = HashMap<String, Profile>()
        val iterator = toFind.iterator()
        while (iterator.hasNext()) {
            val u = iterator.next()
            val profile = getProfile(u)
            if (profile != null) {
                ret[u] = profile
                iterator.remove()
            }
        }
        val sb = StringBuilder("(")
        var first = true
        for (name in names) {
            // check that all usernames are valid to prevent sql injection attempts
            if (!isValidMcUsername(name)) {
                continue
            }
            if (!first) {
                sb.append(", ")
            }
            sb.append("'").append(name).append("'")
            first = false
        }
        if (first) {
            return ret
        }
        sb.append(")")
        return withContext(Dispatchers.IO) {
            try {
                sql.connection.use { c ->
                    c.createStatement().use { s ->
                        s.executeQuery(replaceTableName(String.format(HelperProfileInternal.SELECT_ALL_NAMES, sb.toString()))).use { rs ->
                            while (rs.next()) {
                                val name = rs.getString("name") // provide a case corrected name
                                val lastUpdate = rs.getTimestamp("lastupdate")
                                val uuidString = rs.getString("canonicalid")
                                val uuid = fromString(uuidString)
                                val p = ImmutableProfile(uuid, name, lastUpdate.getTime())
                                updateCache(p)
                                ret[name] = p
                            }
                        }
                    }
                }
            } catch (e: SQLException) {
                e.printStackTrace()
            }
            ret
        }
    }
}