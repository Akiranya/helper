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
package me.lucko.helper.messaging

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import com.google.common.reflect.TypeToken
import kotlinx.coroutines.*
import me.lucko.helper.extension.exceptionHandler
import me.lucko.helper.messaging.codec.KCodec
import me.lucko.helper.messaging.codec.KGZipCodec
import me.lucko.helper.messaging.codec.KGsonCodec
import me.lucko.helper.messaging.codec.KMessage
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.function.BiConsumer

/**
 * An abstract implementation of [Messenger].
 *
 * Outgoing messages are passed to a [BiConsumer] to be passed on.
 *
 * Incoming messages can be distributed using [onIncomingMessage].
 */
open class KAbstractMessenger
/**
 * Creates a new abstract messenger
 *
 * @param outgoingMessages the consumer to pass outgoing messages to
 * @param notifySub the consumer to pass the names of channels which should
 *     be subscribed to
 * @param notifyUnsub the consumer to pass the names of channels which
 *     should be unsubscribed from
 */
constructor(
    // consumer for outgoing messages. accepts in the format [channel name, message]
    private val outgoingMessages: (String, ByteArray) -> Unit,
    // consumer for channel names which should be subscribed to.
    private val notifySub: (String) -> Unit,
    // consumer for channel names which should be unsubscribed from.
    private val notifyUnsub: (String) -> Unit,
) : KMessenger {

    private val channels: LoadingCache<Pair<String, TypeToken<*>>, KAbstractChannel<*>> = CacheBuilder.newBuilder().build(KChannelLoader())

    /**
     * Distributes an oncoming message to the channels held in this messenger.
     *
     * 把收到的消息分发到该 messenger 下创建的所有 channels
     *
     * @param channel the channel the message was received on
     * @param message the message
     */
    suspend fun onIncomingMessage(channel: String, message: ByteArray) {
        for ((key, value) in channels.asMap()) {
            if (key.first == channel) {
                value.onIncomingMessage(message)
            }
        }
    }

    override fun <T> getChannel(name: String, type: TypeToken<T>): KChannel<T> {
        check(name.trim().isNotEmpty()) { "name cannot be empty" }
        @Suppress("UNCHECKED_CAST")
        return channels.getUnchecked(Pair(name, type)) as KAbstractChannel<T>
    }

    private class KAbstractChannel<T>(
        private val messenger: KAbstractMessenger,
        override val name: String,
        override val type: TypeToken<T>,
    ) : KChannel<T> {
        override val codec: KCodec<T> = KGZipCodec(getCodec(type))
        override val channelScope = CoroutineScope(CoroutineName("abstract-channel-$name")) + Dispatchers.IO + SupervisorJob() + exceptionHandler()

        val agents: MutableSet<KAbstractChannelAgent<T>> = ConcurrentHashMap.newKeySet()

        private var subscribed = false

        /** 把收到的消息分发到该 channel 下创建的所有 agents */
        suspend fun onIncomingMessage(message: ByteArray) {
            try {
                val decoded = codec.decode(message)
                requireNotNull(decoded) { "decoded" }
                for (agent in agents) {
                    try {
                        agent.onIncomingMessage(decoded)
                    } catch (e: Exception) {
                        RuntimeException("Unable to pass decoded message to agent: $decoded", e).printStackTrace()
                    }
                }
            } catch (e: Exception) {
                RuntimeException("Unable to decode message: " + Base64.getEncoder().encodeToString(message), e).printStackTrace()
            }
        }

        fun checkSubscription() {
            val shouldSubscribe = agents.any { it.hasListeners() }
            if (shouldSubscribe == subscribed) {
                return
            }
            subscribed = shouldSubscribe

            channelScope.launch {
                try {
                    if (shouldSubscribe) {
                        messenger.notifySub(name)
                    } else {
                        messenger.notifyUnsub(name)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        override fun newAgent(): KChannelAgent<T> {
            val agent = KAbstractChannelAgent(this, channelScope)
            agents.add(agent)
            return agent
        }

        override fun sendMessage(message: T): Deferred<Unit> {
            requireNotNull(message) { "message" }
            return channelScope.async(Dispatchers.IO) {
                val buf = codec.encode(message)
                messenger.outgoingMessages(name, buf)
            }
        }

        override fun close() {
            agents.forEach { it.close() }
        }
    }

    private class KAbstractChannelAgent<T>(
        channel: KAbstractChannel<T>,
        private val channelScope: CoroutineScope,
    ) : KChannelAgent<T> {
        // backing fields
        private var channel0: KAbstractChannel<T>? = channel
        private val listeners0: MutableSet<KChannelListener<T>> = ConcurrentHashMap.newKeySet()

        override val channel: KChannel<T>
            get() {
                checkAgentActive()
                return channel0!!
            }

        override val listeners: Set<KChannelListener<T>>
            get() {
                checkAgentActive()
                return listeners0.toSet()
            }

        private fun checkAgentActive() {
            checkNotNull(channel0) { "agent not active" }
        }

        /** 把收到的消息分发到该 agent 下创建的所有 listeners */
        suspend fun onIncomingMessage(message: T) {
            for (listener in listeners) {
                try {
                    listener.onMessage(this@KAbstractChannelAgent, message)
                } catch (e: Exception) {
                    RuntimeException("Unable to pass decoded message to listener: $listener", e).printStackTrace()
                }
            }
        }

        override fun hasListeners(): Boolean {
            return listeners0.isNotEmpty()
        }

        override fun addListener(listener: KChannelListener<T>): Boolean {
            checkAgentActive()
            return try {
                listeners0.add(listener)
            } finally {
                channelScope.launch {
                    channel0!!.checkSubscription()
                }
            }
        }

        override fun removeListener(listener: KChannelListener<T>): Boolean {
            checkAgentActive()
            return try {
                listeners0.remove(listener)
            } finally {
                channelScope.launch {
                    channel0!!.checkSubscription()
                }
            }
        }

        override fun close() {
            if (channel0 == null) {
                return
            }

            channelScope.launch {
                listeners0.clear()
                channel0!!.agents.remove(this@KAbstractChannelAgent)
                channel0!!.checkSubscription()
                channel0 = null
            }
        }
    }

    private inner class KChannelLoader : CacheLoader<Pair<String, TypeToken<*>>, KAbstractChannel<*>>() {
        override fun load(spec: Pair<String, TypeToken<*>>): KAbstractChannel<*> {
            return KAbstractChannel(this@KAbstractMessenger, spec.first, spec.second)
        }
    }

    companion object CodecProvider {
        private fun <T> getCodec(type: TypeToken<T>): KCodec<T> {
            var rawType = type.getRawType()
            do {
                val message = rawType.getAnnotation(KMessage::class.java)
                if (message != null) {
                    val codec: Class<out KCodec<*>> = message.codec.java
                    try {
                        @Suppress("UNCHECKED_CAST")
                        return codec.getDeclaredConstructor().newInstance() as KCodec<T>
                    } catch (e: ReflectiveOperationException) {
                        e.printStackTrace()
                    }
                }
            } while (rawType.superclass.also { rawType = it } != null)
            return KGsonCodec(type)
        }
    }
}