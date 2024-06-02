package com.juul.exercise.compile

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSNode
import com.juul.khronicle.Key
import com.juul.khronicle.Logger
import com.juul.khronicle.ReadMetadata

internal object Node : Key<KSNode>

internal class KspKhronicleLogger(
    private val backend: KSPLogger,
) : Logger {

    override fun verbose(tag: String, message: String, metadata: ReadMetadata, throwable: Throwable?) =
        callBackend(KSPLogger::logging, tag, message, metadata, throwable)

    override fun debug(tag: String, message: String, metadata: ReadMetadata, throwable: Throwable?) =
        callBackend(KSPLogger::logging, tag, message, metadata, throwable)

    override fun info(tag: String, message: String, metadata: ReadMetadata, throwable: Throwable?) =
        callBackend(KSPLogger::info, tag, message, metadata, throwable)

    override fun warn(tag: String, message: String, metadata: ReadMetadata, throwable: Throwable?) =
        callBackend(KSPLogger::warn, tag, message, metadata, throwable)

    override fun error(tag: String, message: String, metadata: ReadMetadata, throwable: Throwable?) =
        callBackend(KSPLogger::error, tag, message, metadata, throwable)

    override fun assert(tag: String, message: String, metadata: ReadMetadata, throwable: Throwable?) =
        callBackend(KSPLogger::error, tag, message, metadata, throwable)

    private fun callBackend(
        action: KSPLogger.(String, KSNode?) -> Unit,
        tag: String,
        message: String,
        metadata: ReadMetadata,
        throwable: Throwable?,
    ) {
        val node = metadata[Node]
        backend.action("$tag: $message", node)

        if (throwable != null) {
            backend.exception(throwable)
        }
    }

    override fun hashCode(): Int = backend.hashCode()

    override fun equals(other: Any?): Boolean = other is KspKhronicleLogger && backend == other.backend
}
