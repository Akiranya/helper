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

/**
 * Represents an object listening for replies sent on the conversation
 * channel.
 *
 * @param R the reply type
 */
interface KConversationReplyListener<R : KConversationMessage> {
    /**
     * Called when a message is posted to this listener.
     *
     * This method is called asynchronously.
     *
     * @param reply the reply message
     * @return the action to take
     */
    suspend fun onReply(reply: R): RegistrationAction

    /**
     * Called when the listener times out.
     *
     * A listener times out if the "timeout wait period" passes before the
     * listener is unregistered by other means.
     *
     * "unregistered by other means" refers to the listener being
     * stopped after a message was passed to [.onReply] and
     * [RegistrationAction.STOP_LISTENING] being returned.
     *
     * @param replies the replies which have been received
     */
    suspend fun onTimeout(replies: List<R>)

    /**
     * Defines the actions to take after receiving a reply in a
     * [KConversationReplyListener].
     */
    enum class RegistrationAction {
        /** Marks that the listener should continue listening for replies */
        CONTINUE_LISTENING,

        /** Marks that the listener should stop listening for replies */
        STOP_LISTENING
    }

    companion object Factory {
        fun <R : KConversationMessage> of(onReply: (R) -> RegistrationAction): KConversationReplyListener<R> {
            return object : KConversationReplyListener<R> {
                override suspend fun onReply(reply: R): RegistrationAction {
                    return onReply(reply)
                }

                override suspend fun onTimeout(replies: List<R>) {
                    // noop
                }
            }
        }
    }
}
