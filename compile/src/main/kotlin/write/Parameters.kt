package com.juul.exercise.compile.write

import com.juul.exercise.compile.build
import com.juul.exercise.compile.bundleOfMemberName
import com.juul.exercise.compile.data.Parameter
import com.juul.exercise.compile.writeToMarshalledBytesMemberName
import com.juul.exercise.compile.writeToMarshalledBytesOrNullMemberName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.buildCodeBlock

internal fun List<Parameter>.asBundleOf(
    prefix: CodeBlock? = null,
): CodeBlock = when {
    isEmpty() -> CodeBlock.of("%M()", bundleOfMemberName)
    else -> buildCodeBlock {
        add("%M(⇥\n", bundleOfMemberName)
        for ((index, param) in withIndex()) {
            val key = if (prefix == null) param.name else "\${$prefix}.${param.name}"
            val value = if (param.parceler == null) {
                CodeBlock.of(param.name)
            } else {
                val writeMemberName =
                    if (param.optional) writeToMarshalledBytesOrNullMemberName else writeToMarshalledBytesMemberName
                CodeBlock.of("%T.%M(%L)", param.parceler, writeMemberName, param.name)
            }
            add("\"%L\" to %L", key, value)
            if (index + 1 != size) add(",\n")
        }
        add("\n⇤)")
    }
}

internal fun Iterable<Parameter>.asParameterSpecs(): List<ParameterSpec> =
    asSequence()
        .sortedBy { it.optional }
        .map { arg ->
            ParameterSpec.build(arg.name, arg.combinedTypeName) {
                if (arg.optional) {
                    defaultValue("null")
                }
            }
        }.toList()
