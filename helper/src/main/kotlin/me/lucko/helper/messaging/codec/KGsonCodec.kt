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
package me.lucko.helper.messaging.codec

import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.*
import java.nio.charset.StandardCharsets

// TODO 考虑使用 kotlin serializer 而非 gson
//  这也应该能同时把 KAbstractMessenger 里的那些反人类 generic 和 cast 给换掉

/**
 * Implementation of [KCodec] using [Gson].
 *
 * @param M the message type
 */
class KGsonCodec<M>(
    private val gson: Gson,
    private val type: TypeToken<M>,
) : KCodec<M> {
    constructor(type: TypeToken<M>) : this(
        GsonBuilder()
            .serializeNulls()
            .disableHtmlEscaping()
            .create(), type
    )

    override fun encode(message: M): ByteArray {
        val byteOut = ByteArrayOutputStream()
        try {
            OutputStreamWriter(byteOut, StandardCharsets.UTF_8).use { writer ->
                gson.toJson(message, type.type, writer)
            }
        } catch (e: IOException) {
            throw EncodingException(e)
        }
        return byteOut.toByteArray()
    }

    override fun decode(buf: ByteArray): M {
        val byteIn = ByteArrayInputStream(buf)
        try {
            InputStreamReader(byteIn, StandardCharsets.UTF_8).use { reader ->
                return gson.fromJson(reader, type.type)
            }
        } catch (e: IOException) {
            throw EncodingException(e)
        }
    }
}
