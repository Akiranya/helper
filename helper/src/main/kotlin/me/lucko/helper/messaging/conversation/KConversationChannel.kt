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

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import me.lucko.helper.messaging.KChannel
import me.lucko.helper.terminable.Terminable
import kotlin.time.Duration

/**
 * An extension of [KChannel] providing an abstraction for two-way
 * "conversations".
 *
 * @param T 首先发出的消息的类型
 * @param R 后续回复的消息的类型
 */
interface KConversationChannel<T : KConversationMessage, R : KConversationMessage> : Terminable {
    /** Gets the name of the channel. */
    val name: String

    /** Gets the channel for primary outgoing messages. */
    val outgoingChannel: KChannel<T>

    /** Gets the channel replies are sent on. */
    val replyChannel: KChannel<R>

    /** The coroutine scope for this conversation channel. */
    val channelScope: CoroutineScope

    /**
     * Creates a new [KConversationChannelAgent] for this channel.
     *
     * @return a new channel agent.
     */
    fun newAgent(): KConversationChannelAgent<T, R>

    /**
     * Creates a new [KConversationChannelAgent] for this channel, and
     * immediately adds the given [KConversationChannelListener] to it.
     *
     * @param listener the listener to register
     * @return the resultant agent
     */
    fun newAgent(listener: KConversationChannelListener<T, R>): KConversationChannelAgent<T, R> {
        val agent = newAgent()
        agent.addListener(listener)
        return agent
    }

    /**
     * Sends a new message to the channel.
     *
     * This method will return immediately, and the deferred will be completed
     * once the message has been sent.
     *
     * @param message the message to dispatch
     * @param timeout the timeout for the reply listener
     * @param replyListener the reply listener
     * @return a deferred which will complete when the message has sent.
     */
    fun sendMessage(
        message: T,
        timeout: Duration,
        replyListener: KConversationReplyListener<R>,
    ): Deferred<Unit>

    /** 构建一个对话。 */
    fun buildMessage(
        message: T,
        timeout: Duration,
    ): KConversationBuilder<T, R> {
        return KConversationBuilder(this, message, timeout)
    }

    override fun close()
}
