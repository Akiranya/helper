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

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.lucko.helper.Events
import me.lucko.helper.Schedulers
import me.lucko.helper.profiles.KProfileRepository
import me.lucko.helper.profiles.Profile
import me.lucko.helper.sql.Sql
import me.lucko.helper.terminable.TerminableConsumer
import me.lucko.helper.terminable.module.TerminableModule
import me.lucko.helper.utils.Log
import me.lucko.helper.utils.UndashedUuids.fromString
import me.lucko.helper.utils.UndashedUuids.toString
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerLoginEvent
import java.sql.SQLException
import java.sql.Timestamp
import java.util.*
import java.util.regex.Pattern
import javax.annotation.Nonnull
import kotlin.jvm.optionals.getOrNull
import kotlin.time.measureTimedValue

class KHelperProfileRepository(
    private val internal: HelperProfileRepositoryInternal,
    private val sql: Sql,
    private val tableName: String,
    private val preloadAmount: Int,
) : KProfileRepository, TerminableModule {

    companion object {
        private const val CREATE = """
            CREATE TABLE IF NOT EXISTS {table} (
                `uniqueid` BINARY(16) NOT NULL PRIMARY KEY,
                `name` VARCHAR(16) NOT NULL,
                `lastupdate` TIMESTAMP NOT NULL
            );
            """
        private const val INSERT = "INSERT INTO {table} VALUES(UNHEX(?), ?, ?) ON DUPLICATE KEY UPDATE `name` = ?, `lastupdate` = ?"
        private const val SELECT_UID = "SELECT `name`, `lastupdate` FROM {table} WHERE `uniqueid` = UNHEX(?)"
        private const val SELECT_NAME = "SELECT HEX(`uniqueid`) AS `canonicalid`, `name`, `lastupdate` FROM {table} WHERE `name` = ? ORDER BY `lastupdate` DESC LIMIT 1"
        private const val SELECT_ALL = "SELECT HEX(`uniqueid`) AS `canonicalid`, `name`, `lastupdate` FROM {table}"
        private const val SELECT_ALL_RECENT = "SELECT HEX(`uniqueid`) AS `canonicalid`, `name`, `lastupdate` FROM {table} ORDER BY `lastupdate` DESC LIMIT ?"
        private const val SELECT_ALL_UIDS = "SELECT HEX(`uniqueid`) AS `canonicalid`, `name`, `lastupdate` FROM {table} WHERE `uniqueid` IN %s"
        private const val SELECT_ALL_NAMES = "SELECT HEX(`uniqueid`) AS `canonicalid`, `name`, `lastupdate` FROM {table} WHERE `name` IN %s GROUP BY `name` ORDER BY `lastupdate` DESC"

        private val MINECRAFT_USERNAME_PATTERN = Pattern.compile("^\\w{3,16}$")

        private fun isValidMcUsername(s: String): Boolean {
            return MINECRAFT_USERNAME_PATTERN.matcher(s).matches()
        }
    }

    override fun setup(@Nonnull consumer: TerminableConsumer) {
        try {
            sql.connection.use { c ->
                c.createStatement().use { s ->
                    s.execute(replaceTableName(CREATE))
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }

        // preload data
        if (preloadAmount > 0) {
            Log.info("[Profiles] Preloading the most recent $preloadAmount entries...")
            measureTimedValue {
                preload(preloadAmount)
            }.also { timedValue ->
                Log.info("[Profiles] Preloaded " + timedValue.value + " profiles into the cache! - took " + timedValue.duration.inWholeMilliseconds + "ms")
            }
        }

        // observe logins
        Events.subscribe(PlayerLoginEvent::class.java, EventPriority.MONITOR)
            .filter { e: PlayerLoginEvent -> e.result == PlayerLoginEvent.Result.ALLOWED }
            .handler { e: PlayerLoginEvent ->
                val profile = ImmutableProfile(e.player.uniqueId, e.player.name)
                updateCache(profile)
                Schedulers.async().run { saveProfile(profile) }
            }
            .bindWith(consumer)
    }

    private fun replaceTableName(s: String): String {
        return s.replace("{table}", tableName)
    }

    private fun updateCache(profile: ImmutableProfile) {
        val existing = internal.profileMap.getIfPresent(profile.uniqueId)
        if (existing == null || existing.timestamp < profile.timestamp) {
            internal.profileMap.put(profile.uniqueId, profile)
        }
    }

    private fun saveProfile(profile: ImmutableProfile) {
        try {
            sql.connection.use { c ->
                c.prepareStatement(replaceTableName(INSERT)).use { ps ->
                    ps.setString(1, toString(profile.uniqueId))
                    val name = profile.name.getOrNull()
                    ps.setString(2, name!!)
                    ps.setTimestamp(3, Timestamp(profile.timestamp))
                    ps.setString(4, name)
                    ps.setTimestamp(5, Timestamp(profile.timestamp))
                    ps.execute()
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    private fun preload(numEntries: Int): Int {
        var i = 0
        try {
            sql.connection.use { c ->
                c.prepareStatement(replaceTableName(SELECT_ALL_RECENT)).use { ps ->
                    ps.setInt(1, numEntries)
                    ps.executeQuery().use { rs ->
                        while (rs.next()) {
                            val name = rs.getString("name")
                            val lastUpdate = rs.getTimestamp("lastupdate")
                            val uuidString = rs.getString("canonicalid")
                            val uuid = fromString(uuidString)
                            val p = ImmutableProfile(uuid, name, lastUpdate.getTime())
                            updateCache(p)
                            i++
                        }
                    }
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return i
    }

    override fun getProfile(uniqueId: UUID): Profile {
        var profile: Profile? = internal.profileMap.getIfPresent(uniqueId)
        if (profile == null) {
            profile = ImmutableProfile(uniqueId, null, 0)
        }
        return profile
    }

    override fun getProfile(name: String): Profile? {
        for (profile in internal.profileMap.asMap().values) {
            val profileName = profile.name.getOrNull()
            if (profileName != null && profileName.equals(name, ignoreCase = true)) {
                return profile
            }
        }
        return null
    }

    override val knownKProfiles: Collection<Profile>
        get() = Collections.unmodifiableCollection(internal.profileMap.asMap().values)

    override suspend fun lookupProfile(uniqueId: UUID): Profile = withContext(Dispatchers.IO) io@{
        val profile = getProfile(uniqueId)
        if (profile.name != null) {
            return@io profile
        } else {
            try {
                sql.connection.use { c ->
                    c.prepareStatement(replaceTableName(SELECT_UID)).use { ps ->
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
                    c.prepareStatement(replaceTableName(SELECT_NAME)).use { ps ->
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
                c.prepareStatement(replaceTableName(SELECT_ALL)).use { ps ->
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
            if (profile.name != null) {
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
                        s.executeQuery(replaceTableName(String.format(SELECT_ALL_UIDS, sb.toString()))).use { rs ->
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
                        s.executeQuery(replaceTableName(String.format(SELECT_ALL_NAMES, sb.toString()))).use { rs ->
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