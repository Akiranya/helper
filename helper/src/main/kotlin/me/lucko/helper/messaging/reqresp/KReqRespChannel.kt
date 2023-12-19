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

import kotlinx.coroutines.Deferred
import me.lucko.helper.messaging.KChannel
import me.lucko.helper.messaging.conversation.KConversationChannel
import me.lucko.helper.terminable.Terminable

/**
 * A generic request/response handler that can operate over the network.
 *
 * This is a high-level interface, implemented in [KSimpleReqRespChannel]
 * using lower-level [KConversationChannel]s and [KChannel]s.
 *
 * @param Req 请求的类型
 * @param Resp 回复的类型
 */
interface KReqRespChannel<Req, Resp> : Terminable {
    /**
     * Sends a request and returns a [deferred][Deferred] encapsulating the
     * response.
     *
     * The [deferred][Deferred] will complete exceptionally if a response is
     * not received before the timeout expires, by default after 5 seconds.
     *
     * @param req the request object
     * @return a [deferred][Deferred] encapsulating the response
     */
    fun request(req: Req): Deferred<Resp>

    /**
     * Registers a response handler.
     *
     * @param handler the response handler
     */
    fun responseHandler(handler: ResponseHandler<Req, Resp>)

    /**
     * Registers a response handler that returns a [deferred][Deferred].
     *
     * @param handler the response handler
     */
    // FIXME 更好的泛型
    // fun asyncResponseHandler(handler: AsyncResponseHandler<Req, Resp>)

    fun interface ResponseHandler<Req, Resp> {
        fun response(req: Req): Resp?
    }

    // FIXME 更好的泛型
    /*fun interface AsyncResponseHandler<Req, Resp> {
        suspend fun response(req: Req): Deferred<Resp?>
    }*/
}
