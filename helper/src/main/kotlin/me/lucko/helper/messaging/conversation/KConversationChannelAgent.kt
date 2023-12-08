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

import me.lucko.helper.terminable.Terminable

/**
 * Represents an agent for interacting with a [KConversationChannel]s
 * message streams.
 *
 * @param T the channel outgoing message type
 * @param R the channel incoming message type
 */
interface KConversationChannelAgent<T : KConversationMessage, R : KConversationMessage> : Terminable {

    /** Gets the channel this agent is acting for. */
    val channel: KConversationChannel<T, R>

    /** Gets an immutable copy of the listeners currently held by this agent. */
    val listeners: Set<KConversationChannelListener<T, R>>

    /**
     * Gets if this agent has any active listeners.
     *
     * @return true if this agent has listeners
     */
    fun hasListeners(): Boolean

    /**
     * Adds a new listener to the channel;
     *
     * @param listener the listener to add
     * @return true if successful
     */
    fun addListener(listener: KConversationChannelListener<T, R>): Boolean

    /**
     * Removes a listener from the channel.
     *
     * @param listener the listener to remove
     * @return true if successful
     */
    fun removeListener(listener: KConversationChannelListener<T, R>): Boolean

    override fun close()
}
