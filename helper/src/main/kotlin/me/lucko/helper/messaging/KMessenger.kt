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

import com.google.common.reflect.TypeToken
import me.lucko.helper.messaging.conversation.KConversationChannel
import me.lucko.helper.messaging.conversation.KConversationMessage
import me.lucko.helper.messaging.conversation.KSimpleConversationChannel
import me.lucko.helper.messaging.reqresp.KReqRespChannel
import me.lucko.helper.messaging.reqresp.KSimpleReqRespChannel

/** Represents an object which manages messaging [KChannel]s. */
interface KMessenger {
    /**
     * Gets a channel by name.
     *
     * @param name the name of the channel.
     * @param type the channel message typetoken
     * @param T the channel message type
     * @return a channel
     */
    fun <T> getChannel(name: String, type: TypeToken<T>): KChannel<T>

    /**
     * Gets a conversation channel by name.
     *
     * @param name the name of the channel
     * @param type the channel outgoing message typetoken
     * @param replyType the channel incoming (reply) message typetoken
     * @param T the channel message type
     * @param R the channel reply type
     * @return a conversation channel
     */
    fun <T : KConversationMessage, R : KConversationMessage> getConversationChannel(
        name: String,
        type: TypeToken<T>,
        replyType: TypeToken<R>,
    ): KConversationChannel<T, R> {
        return KSimpleConversationChannel(name, this, type, replyType)
    }

    /**
     * Gets a req/resp channel by name.
     *
     * @param name the name of the channel
     * @param reqType the request typetoken
     * @param respType the response typetoken
     * @param <Req> the request type
     * @param <Resp> the response type
     * @return the req/resp channel
     */
    fun <Req, Resp> getReqRespChannel(
        name: String,
        reqType: TypeToken<Req>,
        respType: TypeToken<Resp>,
    ): KReqRespChannel<Req, Resp> {
        return KSimpleReqRespChannel(this, name, reqType, respType)
    }

    /**
     * Gets a channel by name.
     *
     * @param name the name of the channel.
     * @param clazz the channel message class
     * @param <T> the channel message type
     * @return a channel
     */
    fun <T> getChannel(name: String, clazz: Class<T>): KChannel<T> {
        return getChannel(name, TypeToken.of(clazz))
    }

    /**
     * Gets a conversation channel by name.
     *
     * @param name the name of the channel
     * @param clazz the channel outgoing message class
     * @param replyClazz the channel incoming (reply) message class
     * @param <T> the channel message type
     * @param <R> the channel reply type
     * @return a conversation channel
     */
    fun <T : KConversationMessage, R : KConversationMessage> getConversationChannel(
        name: String,
        clazz: Class<T>,
        replyClazz: Class<R>,
    ): KConversationChannel<T, R> {
        return getConversationChannel(
            name,
            TypeToken.of(clazz),
            TypeToken.of(replyClazz)
        )
    }

    /**
     * Gets a req/resp channel by name.
     *
     * @param name the name of the channel
     * @param reqClass the request class
     * @param respClass the response class
     * @param Req the request type
     * @param Resp the response type
     * @return the req/resp channel
     */
    fun <Req, Resp> getReqRespChannel(
        name: String,
        reqClass: Class<Req>,
        respClass: Class<Resp>,
    ): KReqRespChannel<Req, Resp> {
        return getReqRespChannel(
            name,
            TypeToken.of(reqClass),
            TypeToken.of(respClass)
        )
    }
}