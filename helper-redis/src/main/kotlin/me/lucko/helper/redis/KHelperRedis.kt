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
package me.lucko.helper.redis

import com.google.common.reflect.TypeToken
import kotlinx.coroutines.*
import me.lucko.helper.extension.exceptionHandler
import me.lucko.helper.messaging.KAbstractMessenger
import me.lucko.helper.messaging.KChannel
import me.lucko.helper.terminable.composite.CompositeTerminable
import me.lucko.helper.utils.Log
import redis.clients.jedis.BinaryJedisPubSub
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import java.nio.charset.StandardCharsets
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class KHelperRedis(
    credentials: RedisCredentials,
) : KRedis {

    override val jedisPool: JedisPool

    override val jedis: Jedis
        get() = jedisPool.resource

    private val redisScope = CoroutineScope(CoroutineName("redis")) + Dispatchers.IO + SupervisorJob() + exceptionHandler()

    override fun close() {
        redisScope.cancel()

        if (listener != null) {
            listener!!.unsubscribe()
            listener = null
        }

        jedisPool.close()
        registry.close()
    }

    override fun <T> getChannel(name: String, type: TypeToken<T>): KChannel<T> {
        return messenger.getChannel(name, type)
    }

    private val messenger: KAbstractMessenger
    private val channels: MutableSet<String> = HashSet()
    private val registry: CompositeTerminable = CompositeTerminable.create()
    private var listener: PubSubListener? = null

    init {
        val config = JedisPoolConfig().apply {
            maxTotal = 16
        }

        // setup jedis
        jedisPool = if (credentials.password.trim().isEmpty()) {
            JedisPool(config, credentials.address, credentials.port)
        } else {
            JedisPool(config, credentials.address, credentials.port, 2000, credentials.password)
        }.apply {
            resource.use { jedis -> jedis.ping() }
        }

        redisScope.launch {
            var broken = false
            do {
                if (broken) {
                    Log.info("[helper-redis] [kotlin] Retrying subscription...")
                    broken = false
                }

                jedis.use { jedis ->
                    try {
                        listener = PubSubListener()
                        jedis.subscribe(listener, "redis-dummy".toByteArray(StandardCharsets.UTF_8))
                    } catch (e: Exception) {
                        // Attempt to unsubscribe this instance and try again.
                        RuntimeException("Error subscribing to listener", e).printStackTrace()
                        runCatching { listener!!.unsubscribe() }
                        listener = null
                        broken = true
                    }
                }

                delay(50L)
            } while (broken) // if broken, try to connect to redis again
        }

        redisScope.launch {
            while (true) {
                delay(100L)

                // ensure subscribed to all channels
                val listener = listener
                if (listener == null || !listener.isSubscribed) {
                    return@launch
                }
                for (channel in channels) {
                    listener.subscribe(channel.toByteArray(StandardCharsets.UTF_8))
                }

                delay(100L)
            }
        }

        messenger = KAbstractMessenger(
            outgoingMessages = { channel, message ->
                jedis.use { jedis -> jedis.publish(channel.toByteArray(StandardCharsets.UTF_8), message) }
            },

            notifySub = { channel ->
                Log.info("[helper-redis] [kotlin] Subscribing to channel: $channel")
                channels.add(channel)
                listener!!.subscribe(channel.toByteArray(StandardCharsets.UTF_8))
            },

            notifyUnsub = { channel ->
                Log.info("[helper-redis] [kotlin] Unsubscribing from channel: $channel")
                channels.remove(channel)
                listener!!.unsubscribe(channel.toByteArray(StandardCharsets.UTF_8))
            }
        )
    }

    private inner class PubSubListener : BinaryJedisPubSub() {
        private val lock = ReentrantLock()
        private val subscribed: MutableSet<String> = ConcurrentHashMap.newKeySet()

        override fun subscribe(vararg channels: ByteArray) {
            lock.withLock {
                for (channel in channels) {
                    val channelName = channel.toString(StandardCharsets.UTF_8)
                    if (subscribed.add(channelName)) {
                        super.subscribe(channel)
                    }
                }
            }
        }

        override fun unsubscribe(vararg channels: ByteArray) {
            lock.withLock {
                super.unsubscribe(*channels)
            }
        }

        override fun onSubscribe(channel: ByteArray, subscribedChannels: Int) {
            Log.info("[helper-redis] [kotlin] Subscribed to channel: " + channel.toString(StandardCharsets.UTF_8))
        }

        override fun onUnsubscribe(channel: ByteArray, subscribedChannels: Int) {
            val channelName = channel.toString(StandardCharsets.UTF_8)
            Log.info("[helper-redis] [kotlin] Unsubscribed from channel: $channelName")
            subscribed.remove(channelName)
        }

        override fun onMessage(channel: ByteArray, message: ByteArray) {
            val channelName = channel.toString(StandardCharsets.UTF_8)
            try {
                redisScope.launch {
                    messenger.onIncomingMessage(channelName, message)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
