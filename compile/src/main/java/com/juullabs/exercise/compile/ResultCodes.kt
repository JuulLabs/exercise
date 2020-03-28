package com.juullabs.exercise.compile

import com.squareup.kotlinpoet.CodeBlock

internal fun reifyResultCode(code: Int): CodeBlock = when (code) {
    -1 -> CodeBlock.of("%T.RESULT_OK", activityTypeName)
    0 -> CodeBlock.of("%T.RESULT_CANCELED", activityTypeName)
    1 -> CodeBlock.of("%T.RESULT_FIRST_USER", activityTypeName)
    else -> CodeBlock.of("Activity.RESULT_FIRST_USER + ${code - 1}", activityTypeName)
}

