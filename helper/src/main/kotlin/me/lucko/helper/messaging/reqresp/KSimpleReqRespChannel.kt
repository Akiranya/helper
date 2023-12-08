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
package me.lucko.helper.messaging.reqresp

import com.google.common.reflect.TypeParameter
import com.google.common.reflect.TypeToken
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import me.lucko.helper.messaging.KMessenger
import me.lucko.helper.messaging.conversation.KConversationChannel
import me.lucko.helper.messaging.conversation.KConversationReply
import me.lucko.helper.messaging.conversation.KConversationReplyListener
import me.lucko.helper.messaging.reqresp.KReqRespChannel.ResponseHandler
import java.util.*
import java.util.concurrent.TimeoutException
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/**
 * Implements a [KReqRespChannel] using [KConversationChannel]s.
 *
 * @param Req the request type
 * @param Resp the response type
 */
class KSimpleReqRespChannel<Req, Resp>(
    messenger: KMessenger,
    name: String,
    reqType: TypeToken<Req>,
    respType: TypeToken<Resp>,
) : KReqRespChannel<Req, Resp> {

    private val channel: KConversationChannel<KReqResMessage<Req>, KReqResMessage<Resp>>

    init {
        val reqMsgType: TypeToken<KReqResMessage<Req>> = object : TypeToken<KReqResMessage<Req>>() {}.where(
            object : TypeParameter<Req>() {}, reqType
        )
        val respMsgType: TypeToken<KReqResMessage<Resp>> = object : TypeToken<KReqResMessage<Resp>>() {}.where(
            object : TypeParameter<Resp>() {}, respType
        )
        channel = messenger.getConversationChannel(name, reqMsgType, respMsgType)
    }

    override fun request(req: Req): Deferred<Resp> {
        val msg = KReqResMessage(UUID.randomUUID(), req)
        val deferred = CompletableDeferred<Resp>()
        channel.sendMessage(msg, 5.toDuration(DurationUnit.SECONDS),
            object : KConversationReplyListener<KReqResMessage<Resp>> {
                override suspend fun onReply(reply: KReqResMessage<Resp>): KConversationReplyListener.RegistrationAction {
                    deferred.complete(reply.body)
                    return KConversationReplyListener.RegistrationAction.STOP_LISTENING
                }

                override suspend fun onTimeout(replies: List<KReqResMessage<Resp>>) {
                    deferred.completeExceptionally(TimeoutException("请求超时"))
                }
            })
        return deferred
    }

    override fun responseHandler(handler: ResponseHandler<Req, Resp>) {
        channel.newAgent agent@{ _, message ->
            val id = message.conversationId
            val req = message.body
            val resp: Resp? = handler.response(req)
            if (resp != null) {
                return@agent KConversationReply.of(KReqResMessage(id, resp))
            } else {
                return@agent KConversationReply.noReply()
            }
        }
    }

    /*override fun asyncResponseHandler(handler: AsyncResponseHandler<Req, Resp>) {
        channel.newAgent agent@{ _, message ->
            val id = message.conversationId
            val req = message.body
            val deferred = handler.response(req)
            if (deferred != null) {
                val result = channel.channelScope.async {
                    val resp: Resp? = deferred.await()
                    if (resp != null) {
                        KReqResMessage(id, resp)
                    } else {
                        null
                    }
                }
                KConversationReply.noReply() // FIXME 更好的泛型
                // KConversationReply.ofDeferred(result)
            } else {
                KConversationReply.noReply()
            }
        }
    }*/

    override fun close() {
        channel.close()
    }
}
