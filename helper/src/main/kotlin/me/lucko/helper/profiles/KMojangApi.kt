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

import com.google.gson.stream.JsonReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.lucko.helper.utils.KUndashedUuids
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.util.*

/** Utilities for interacting with the Mojang API. */
@Deprecated("API subject to change")
object KMojangApi {
    private const val PROFILES_URL = "https://api.mojang.com/users/profiles/minecraft/%s"
    private const val NAME_HISTORY_URL = "https://api.mojang.com/user/profiles/%s/names"

    /**
     * Gets the online [UUID] of a player by fetching it from the Mojang API.
     * The Mojang API has a limit of 600 requests per 10 minutes.
     *
     * @param username the username of the player from which to get the [UUID]
     * @return the online [UUID] of the player with the supplied name
     */
    suspend fun usernameToUuid(username: String): UUID? = withContext(Dispatchers.IO) io@{
        require(username.isNotEmpty()) { "empty" }
        return@io try {
            val url = URL(String.format(PROFILES_URL, username))
            val jsonReader = JsonReader(InputStreamReader(url.openConnection().getInputStream()))
            jsonReader.use { reader ->
                reader.beginObject()
                reader.skipValue()
                KUndashedUuids.fromString(reader.nextString())
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Gets the online username of the player with the supplied [UUID] by
     * fetching it from the Mojang API. The Mojang API has a limit of 600
     * requests per 10 minutes.
     *
     * @param uuid the [UUID] of the player from which to get the name
     * @return the online username of the player with the supplied uuid, or
     *     null if it doesn't exist
     */
    suspend fun uuidToUsername(uuid: UUID): String? {
        val names = getUsernameHistory(uuid)
        return if (names.isNotEmpty()) {
            names[names.size - 1]
        } else {
            null
        }
    }

    /**
     * Gets the history of the names owned by the player with the supplied
     * [UUID], in chronological order, fetching it from the Mojang
     * API. The Mojang API has a limit of 600 requests per 10 minutes.
     *
     * @param uuid the [UUID] of the player from which to fetch the name
     *     history
     * @return a list string with all the names owned by the player with the
     *     supplied uuid, in chronological order
     */
    suspend fun getUsernameHistory(uuid: UUID): List<String> = withContext(Dispatchers.IO) io@{
        val names = arrayListOf<String>()
        try {
            val url = URL(String.format(NAME_HISTORY_URL, KUndashedUuids.toString(uuid)))
            val jsonReader = JsonReader(InputStreamReader(url.openConnection().getInputStream()))
            jsonReader.use { reader ->
                reader.beginArray()
                var i = 0
                while (reader.hasNext()) {
                    reader.beginObject()
                    reader.skipValue()
                    names.add(reader.nextString())
                    if (i != 0) {
                        reader.skipValue()
                        reader.skipValue()
                    }
                    reader.endObject()
                    i++
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return@io names
    }
}
