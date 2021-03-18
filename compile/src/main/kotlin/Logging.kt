package com.juul.exercise.compile

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSNode
import com.juul.tuulbox.logging.Logger

/** Interface indicating that a throwable has a [KSNode] for the logger to associate the message with. */
internal interface HasKSNode {
    val node: KSNode
}

/** Dirty hack to pass [KSNode] into Tuulbox logging for non-exceptional cases. */
internal class NonExceptionalNode(override val node: KSNode) : Throwable(), HasKSNode

internal class ExceptionalNode(cause: Throwable, override val node: KSNode) : Exception(cause), HasKSNode

internal class KspTuulboxLogger(
    private val backend: KSPLogger
) : Logger {

    override fun verbose(tag: String, message: String, throwable: Throwable?) =
        callBackend(KSPLogger::logging, tag, message, throwable)

    override fun debug(tag: String, message: String, throwable: Throwable?) =
        callBackend(KSPLogger::logging, tag, message, throwable)

    override fun info(tag: String, message: String, throwable: Throwable?) =
        callBackend(KSPLogger::info, tag, message, throwable)

    override fun warn(tag: String, message: String, throwable: Throwable?) =
        callBackend(KSPLogger::warn, tag, message, throwable)

    override fun error(tag: String, message: String, throwable: Throwable?) =
        callBackend(KSPLogger::error, tag, message, throwable)

    override fun assert(tag: String, message: String, throwable: Throwable?) =
        callBackend(KSPLogger::error, tag, message, throwable)

    private fun callBackend(
        action: KSPLogger.(String, KSNode?) -> Unit,
        tag: String,
        message: String,
        throwable: Throwable?
    ) {
        val node = (throwable as? HasKSNode)?.node
        backend.action("$tag: $message", node)

        if (throwable != null && throwable !is NonExceptionalNode) {
            backend.exception(throwable)
        }
    }
}

/** Convert a [KSNode] into a special-case throwable so it can be included in a log without reporting an exception. */
internal fun KSNode.loggable() = NonExceptionalNode(this)

/** Convert a [KSNode] into a special-case throwable so it can be included in a log without reporting an exception. */
internal fun Throwable.withNode(node: KSNode) = ExceptionalNode(this, node)
