package me.lucko.helper.messaging.conversation

import kotlinx.coroutines.Deferred
import me.lucko.helper.messaging.conversation.KConversationReplyListener.RegistrationAction
import kotlin.time.Duration

/**
 * Conversation builder.
 *
 * @param T 首先发出的消息的类型
 * @param R 后续发出的消息的类型
 * @constructor Create empty K conversation builder
 * @property message
 */
class KConversationBuilder<T : KConversationMessage, R : KConversationMessage>(
    val channel: KConversationChannel<T, R>,
    val message: T,
    val timeout: Duration,
) {
    private lateinit var onReply0: suspend (reply: R) -> RegistrationAction
    private lateinit var onTimeout0: suspend (replies: List<R>) -> Unit

    fun onReply(block: suspend (reply: R) -> RegistrationAction): KConversationBuilder<T, R> {
        onReply0 = block
        return this
    }

    fun onTimeout(block: suspend (replies: List<R>) -> Unit): KConversationBuilder<T, R> {
        onTimeout0 = block
        return this
    }

    private fun send0(): Deferred<Unit> {
        check(this::onReply0.isInitialized) { "onReply 未初始化" }
        check(this::onTimeout0.isInitialized) { "onTimeout 未初始化" }

        return channel.sendMessage(message, timeout, object : KConversationReplyListener<R> {
            override suspend fun onReply(reply: R): RegistrationAction {
                return onReply0(reply)
            }

            override suspend fun onTimeout(replies: List<R>) {
                onTimeout0(replies)
            }
        })
    }

    fun send() {
        send0()
    }

    suspend fun sendAndAwait() {
        send0().await()
    }
}
