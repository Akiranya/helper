package me.lucko.helper.messaging.extension

import com.google.common.reflect.TypeToken
import me.lucko.helper.messaging.KChannel
import me.lucko.helper.messaging.KMessenger
import me.lucko.helper.messaging.conversation.KConversationChannel
import me.lucko.helper.messaging.conversation.KConversationMessage
import me.lucko.helper.messaging.reqresp.KReqRespChannel
import me.lucko.helper.messaging.reqresp.KSimpleReqRespChannel

inline fun <reified T> KMessenger.getChannel(
    name: String,
): KChannel<T> {
    return getChannel(name, TypeToken.of(T::class.java))
}

inline fun <reified T : KConversationMessage, reified R : KConversationMessage> KMessenger.getConversationChannel(
    name: String,
): KConversationChannel<T, R> {
    return getConversationChannel(name, TypeToken.of(T::class.java), TypeToken.of(R::class.java))
}

inline fun <reified Req, reified Resp> KMessenger.getReqRespChannel(
    name: String,
): KReqRespChannel<Req, Resp> {
    return KSimpleReqRespChannel(this, name, TypeToken.of(Req::class.java), TypeToken.of(Resp::class.java))
}