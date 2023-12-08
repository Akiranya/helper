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

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.future.asDeferred
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Encapsulates the reply to an incoming message in a
 * [KConversationChannel].
 *
 * @param R the reply type
 */
class KConversationReply<R : KConversationMessage>
private constructor(
    private val reply: Deferred<R>,
) {
    companion object Factory {
        // 表示“无回复”的 dummy object
        private val NO_REPLY: KConversationReply<*> = KConversationReply(CompletableDeferred(null))

        /**
         * Returns an object indicating that no reply should be sent.
         *
         * @param R the reply type
         * @return a "no reply" marker
         */
        fun <R : KConversationMessage> noReply(): KConversationReply<R> {
            @Suppress("UNCHECKED_CAST")
            return NO_REPLY as KConversationReply<R>
        }

        /**
         * Creates a new [KConversationReply].
         *
         * @param reply the reply message
         * @param R the type
         * @return the new reply encapsulation
         */
        fun <R : KConversationMessage> of(reply: R): KConversationReply<R> {
            return KConversationReply(CompletableDeferred(reply))
        }

        /**
         * Creates a new [KConversationReply].
         *
         * Bear in mind the reply will only be sent once the [deferredReply]
         * completes. The timeout value on "other end" may need to take this into
         * account.
         *
         * @param deferredReply the future reply
         * @param R the type
         * @return the new reply encapsulation </R>
         */
        fun <R : KConversationMessage> ofDeferred(deferredReply: Deferred<R>): KConversationReply<R> {
            return KConversationReply(deferredReply)
        }

        /**
         * Creates a new [KConversationReply].
         *
         * Bear in mind the reply will only be sent once the [futureReply]
         * completes. The timeout value on "other end" may need to take this into
         * account.
         *
         * @param futureReply the future reply
         * @param R the type
         * @return the new reply encapsulation
         */
        fun <R : KConversationMessage> ofCompletableFuture(futureReply: CompletableFuture<R>): KConversationReply<R> {
            return KConversationReply(futureReply.asDeferred())
        }
    }

    /**
     * Gets if this object actually contains a reply.
     *
     * @return if the object has a reply
     */
    fun hasReply(): Boolean {
        return this != NO_REPLY
    }

    /**
     * Gets the reply.
     *
     * @return the reply
     * @throws IllegalStateException if [hasReply] returns false
     */
    fun getReply(): Deferred<R> {
        check(hasReply()) { "No reply present" }
        return reply
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is KConversationReply<*>) {
            return false
        }
        return reply == other.reply
    }

    override fun hashCode(): Int {
        return Objects.hashCode(reply)
    }

    override fun toString(): String {
        return if (hasReply()) "ConversationReply[$reply]" else "ConversationReply.noReply"
    }
}

fun <R : KConversationMessage> R.toReply(): KConversationReply<R> {
    return KConversationReply.of(this)
}

fun <R : KConversationMessage> Deferred<R>.toReply(): KConversationReply<R> {
    return KConversationReply.ofDeferred(this)
}
