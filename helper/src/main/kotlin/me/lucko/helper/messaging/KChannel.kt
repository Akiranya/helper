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

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import me.lucko.helper.interfaces.KTypeAware
import me.lucko.helper.messaging.codec.KCodec
import me.lucko.helper.terminable.Terminable

/**
 * Represents an individual messaging channel.
 *
 * Channels can be subscribed to through a [KChannelAgent].
 *
 * @param T the channel message type
 */
interface KChannel<T> : KTypeAware<T>, Terminable {
    /** Gets the name of the channel. */
    val name: String

    /** Gets the channels codec. */
    val codec: KCodec<T>

    /** The coroutine scope for this channel. */
    val channelScope: CoroutineScope

    /**
     * Creates a new [KChannelAgent] for this channel.
     *
     * @return a new channel agent.
     */
    fun newAgent(): KChannelAgent<T>

    /**
     * Creates a new [KChannelAgent] for this channel, and immediately adds the
     * given [KChannelListener] to it.
     *
     * @param listener the listener to register
     * @return the resultant agent
     */
    fun newAgent(listener: KChannelListener<T>): KChannelAgent<T> {
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
     */
    fun sendMessage(message: T): Deferred<Unit>
}
