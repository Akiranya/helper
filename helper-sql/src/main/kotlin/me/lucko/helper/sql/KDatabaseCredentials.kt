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

package me.lucko.helper.sql

import org.bukkit.configuration.ConfigurationSection

/** Represents the credentials for a remote database. */
class KDatabaseCredentials(
    val host: String,
    val port: Int,
    val database: String,
    val username: String,
    val password: String,
    val useSsl: Boolean,
) {
    constructor(
        config: ConfigurationSection,
    ) : this(
        requireNotNull(config.getString("host", "localhost")),
        requireNotNull(config.getInt("port", 3306)),
        requireNotNull(config.getString("database", "minecraft")),
        requireNotNull(config.getString("username", "root")),
        requireNotNull(config.getString("password", "passw0rd")),
        requireNotNull(config.getBoolean("use_ssl", false))
    )

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        return if (other !is KDatabaseCredentials) {
            false
        } else {
            host == other.host
                    && port == other.port
                    && database == other.database
                    && username == other.username
                    && password == other.password
                    && useSsl == other.useSsl
        }
    }

    override fun hashCode(): Int {
        val prime = 59
        var result = 1
        result = result * prime + port
        result = result * prime + host.hashCode()
        result = result * prime + database.hashCode()
        result = result * prime + username.hashCode()
        result = result * prime + password.hashCode()
        result = result * prime + useSsl.hashCode()
        return result
    }

    override fun toString(): String {
        return "DatabaseCredentials(" +
                "host=" + host + ", " +
                "port=" + port + ", " +
                "database=" + database + ", " +
                "username=" + username + ", " +
                "password=" + password + ", " +
                "use_ssl=" + useSsl + ")"
    }
}
