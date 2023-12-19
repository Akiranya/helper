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

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.min

class KHelperSql(
    credentials: KDatabaseCredentials,
) : KSql {
    companion object {
        private val POOL_COUNTER = AtomicInteger(0)

        // https://github.com/brettwooldridge/HikariCP/wiki/About-Pool-Sizing
        private val MAXIMUM_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2 + 1
        private val MINIMUM_IDLE = min(MAXIMUM_POOL_SIZE.toDouble(), 10.0).toInt()

        private val MAX_LIFETIME = TimeUnit.MINUTES.toMillis(30)
        private val CONNECTION_TIMEOUT = TimeUnit.SECONDS.toMillis(10)
        private val LEAK_DETECTION_THRESHOLD = TimeUnit.SECONDS.toMillis(10)
    }

    override val hikari: HikariDataSource

    init {
        val hikari = HikariConfig().apply {
            poolName = "helper-sql-" + POOL_COUNTER.getAndIncrement()

            driverClassName = "com.mysql.cj.jdbc.Driver"
            jdbcUrl =
                "jdbc:mysql://${credentials.host}:${credentials.port}/${credentials.database}?allowPublicKeyRetrieval=true&useSSL=${credentials.useSsl}"

            username = credentials.username
            password = credentials.password

            maximumPoolSize = MAXIMUM_POOL_SIZE
            minimumIdle = MINIMUM_IDLE

            maxLifetime = MAX_LIFETIME
            connectionTimeout = CONNECTION_TIMEOUT
            leakDetectionThreshold = LEAK_DETECTION_THRESHOLD

            val properties = buildMap {
                // Ensure we use utf8 encoding
                put("useUnicode", "true")
                put("characterEncoding", "utf8")

                // https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration
                put("cachePrepStmts", "true")
                put("prepStmtCacheSize", "250")
                put("prepStmtCacheSqlLimit", "2048")
                put("useServerPrepStmts", "true")
                put("useLocalSessionState", "true")
                put("rewriteBatchedStatements", "true")
                put("cacheResultSetMetadata", "true")
                put("cacheServerConfiguration", "true")
                put("elideSetAutoCommits", "true")
                put("maintainTimeStats", "false")
                put("alwaysSendSetIsolation", "false")
                put("cacheCallableStmts", "true")

                // Set the driver level TCP socket timeout
                // See: https://github.com/brettwooldridge/HikariCP/wiki/Rapid-Recovery
                put("socketTimeout", TimeUnit.SECONDS.toMillis(30).toString())
            }

            for ((key, value) in properties) {
                addDataSourceProperty(key, value)
            }
        }

        this.hikari = HikariDataSource(hikari)
    }

    override val connection: Connection
        get() = requireNotNull(hikari.connection) { "connection is null" }

    override fun close() {
        hikari.close()
    }
}