package me.lucko.helper.extension

import kotlinx.coroutines.CoroutineExceptionHandler
import org.slf4j.Logger

/** Creates a root exception handler for coroutine scope. */
fun exceptionHandler(logger: Logger) = CoroutineExceptionHandler { _, e ->
    logger.error("处理消息时发生了一个未捕获的错误", e)
}