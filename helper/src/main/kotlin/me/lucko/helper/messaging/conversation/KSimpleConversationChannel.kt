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
package me.lucko.helper.messaging.conversation

import com.google.common.collect.ImmutableSet
import com.google.common.collect.Multimaps
import com.google.common.collect.SetMultimap
import com.google.common.reflect.TypeToken
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import me.lucko.helper.extension.exceptionHandler
import me.lucko.helper.messaging.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/**
 * Simple implementation of [KConversationChannel].
 *
 * @param T the outgoing message type
 * @param R the reply message type
 */
class KSimpleConversationChannel<T : KConversationMessage, R : KConversationMessage>(
    override val name: String,
    messenger: KMessenger,
    outgoingType: TypeToken<T>,
    replyType: TypeToken<R>,
) : KConversationChannel<T, R> {
    override val outgoingChannel: KChannel<T> = messenger.getChannel("$name-o", outgoingType)
    override val replyChannel: KChannel<R> = messenger.getChannel("$name-r", replyType)
    override val channelScope = CoroutineScope(CoroutineName("conversation-channel-$name")) + Dispatchers.IO + SupervisorJob() + exceptionHandler()

    private val agents: MutableSet<Agent<T, R>> = ConcurrentHashMap.newKeySet()
    private val timeoutScope = CoroutineScope(CoroutineName("conversation-channel-$name-timeout")) + Dispatchers.IO + SupervisorJob() + exceptionHandler()
    private val replyListeners: SetMultimap<UUID, ReplyListenerRegistration<R>> = Multimaps.newSetMultimap(ConcurrentHashMap()) { ConcurrentHashMap.newKeySet() }
    private val replyAgent: KChannelAgent<R> = replyChannel.newAgent(ReplyListener())

    // ReplyListener 的生命周期同 replyChannel 一致
    // 类似一个消息的分发者，会把收到的消息分发给每一个对话
    private inner class ReplyListener : KChannelListener<R> {
        // 每当 replyChannel 收到消息时，都会调用这里的 onMessage
        // 然后根据回复的消息决定是否移除 ReplyListenerRegistration

        // 如果回复是 STOP_LISTENING 则移除
        // 如果回复是 CONTINUE_LISTENING 则保留
        override suspend fun onMessage(agent: KChannelAgent<R>, message: R) {
            val listeners = replyListeners[message.conversationId]
            val each = listeners.iterator()
            while (each.hasNext()) {
                if (each.next().onReply(message)) {
                    each.remove()
                }
            }
        }
    }

    private class ReplyListenerRegistration<R : KConversationMessage>(
        private val listener: KConversationReplyListener<R>,
    ) {
        private val mutex = Mutex()
        private val replies: MutableList<R> = ArrayList()
        private var active: Boolean = true

        // 负责调用 timeout() 的 Job
        var timeoutJob: Job? = null

        /**
         * Passes the incoming reply to the listener, and returns true if the
         * listener should be unregistered
         *
         * @param message the message
         * @return if the listener should be unregistered
         */
        suspend fun onReply(message: R): Boolean {
            mutex.withLock {
                if (!active) {
                    return true
                }
                replies.add(message)
                val action = listener.onReply(message)
                return if (action == KConversationReplyListener.RegistrationAction.STOP_LISTENING) {
                    // unregister it
                    active = false
                    requireNotNull(timeoutJob) { "timeoutJob 为空" }.cancel()
                    true
                } else {
                    false
                }
            }
        }

        suspend fun timeout() {
            mutex.withLock {
                if (!active) {
                    return
                }
                listener.onTimeout(replies)
                active = false
            }
        }
    }

    override fun newAgent(): KConversationChannelAgent<T, R> {
        val agent = Agent(this)
        agents.add(agent)
        return agent
    }

    override fun sendMessage(
        message: T,
        timeout: Duration,
        replyListener: KConversationReplyListener<R>,
    ): Deferred<Unit> {
        // register the listener
        val listenerRegistration = ReplyListenerRegistration(replyListener)

        // 安排一个 Job 负责在超时发生时调用 timeout()
        val timeoutJob = timeoutScope.launch {
            // 稍微比设定的超时晚一点，抵消启动协程发消息所花费的时间
            delay(timeout.plus(25.toDuration(DurationUnit.MILLISECONDS)))
            listenerRegistration.timeout()
        }

        // 给 timeoutJob 赋值，以便让 ReplyListenerRegistration 可以自己提前取消 timeoutJob
        listenerRegistration.timeoutJob = timeoutJob

        replyListeners.put(message.conversationId, listenerRegistration)

        // send the outgoing message
        return outgoingChannel.sendMessage(message)
    }

    override fun close() {
        replyAgent.close()
        channelScope.cancel()
        timeoutScope.cancel()
        agents.forEach { obj: Agent<T, R> -> obj.close() }
    }

    private class Agent<T : KConversationMessage, R : KConversationMessage>(
        channel: KSimpleConversationChannel<T, R>,
    ) : KConversationChannelAgent<T, R> {

        // backing field of 'channel'
        private val channel0: KSimpleConversationChannel<T, R> = channel

        // 从主动方发送消息用的 channel 创建一个 agent
        private val delegateAgent: KChannelAgent<T> = channel.outgoingChannel.newAgent()

        override val channel: KConversationChannel<T, R>
            get() {
                delegateAgent.channel // ensure this agent is still active
                return channel0
            }

        override val listeners: Set<KConversationChannelListener<T, R>>
            get() {
                val listeners = delegateAgent.listeners
                val ret = ImmutableSet.builder<KConversationChannelListener<T, R>>()
                for (listener in listeners) {
                    @Suppress("UNCHECKED_CAST")
                    ret.add((listener as Agent<T, R>.WrappedListener).delegate)
                }
                return ret.build()
            }

        override fun hasListeners(): Boolean {
            return delegateAgent.hasListeners()
        }

        override fun addListener(listener: KConversationChannelListener<T, R>): Boolean {
            // 把 listener 加到 outgoingChannel 的 agent
            return delegateAgent.addListener(WrappedListener(listener))
        }

        override fun removeListener(listener: KConversationChannelListener<T, R>): Boolean {
            val listeners = delegateAgent.listeners
            for (other in listeners) {
                @Suppress("UNCHECKED_CAST")
                val wrapped: WrappedListener = other as Agent<T, R>.WrappedListener
                if (wrapped.delegate === listener) {
                    return delegateAgent.removeListener(other)
                }
            }
            return false
        }

        override fun close() {
            delegateAgent.close()
        }

        inner class WrappedListener(
            // 监听主动方发送消息的 listener
            val delegate: KConversationChannelListener<T, R>,
        ) : KChannelListener<T> {
            /** 把从 ChannelListener 收到的消息委托给 ConversationChannelListener */
            override suspend fun onMessage(agent: KChannelAgent<T>, message: T) {
                val reply = delegate.onMessage(this@Agent, message)
                if (reply.hasReply()) {
                    channel.channelScope.launch {
                        val m = reply.getReply().await()
                        // 发送消息到 replyChannel 以回复主动方
                        channel.replyChannel.sendMessage(m).await()
                    }
                }
            }
        }
    }
}
