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

import com.google.common.io.ByteStreams
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

/**
 * A [Codec] wrapper using GZip.
 *
 * @param M the message type
 */
class KGZipCodec<M>(
    private val delegate: KCodec<M>,
) : KCodec<M> {
    override fun encode(message: M): ByteArray {
        val byteIn = delegate.encode(message)
        val byteOut = ByteArrayOutputStream()
        try {
            GZIPOutputStream(byteOut).use { gzipOut ->
                gzipOut.write(byteIn)
            }
        } catch (e: IOException) {
            throw EncodingException(e)
        }
        return byteOut.toByteArray()
    }

    override fun decode(buf: ByteArray): M {
        var uncompressed: ByteArray
        try {
            GZIPInputStream(ByteArrayInputStream(buf)).use { gzipIn ->
                uncompressed = ByteStreams.toByteArray(gzipIn)
            }
        } catch (e: IOException) {
            throw EncodingException(e)
        }
        return delegate.decode(uncompressed)
    }
}
