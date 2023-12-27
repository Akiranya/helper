package me.lucko.helper.extension

import kotlinx.coroutines.CoroutineExceptionHandler
import me.lucko.helper.utils.Log

/** Creates a root exception handler for coroutine scope. */
fun exceptionHandler() = CoroutineExceptionHandler { _, e ->
    Log.severe("处理消息时发生了一个未捕获的错误", e)
}