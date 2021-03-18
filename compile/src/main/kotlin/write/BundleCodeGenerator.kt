package com.juul.exercise.compile.write

import com.juul.exercise.compile.addFunction
import com.juul.exercise.compile.bundleTypeName
import com.juul.exercise.compile.data.Parameter
import com.juul.exercise.compile.data.Receiver
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.buildCodeBlock

internal class BundleCodeGenerator(
    private val receiver: Receiver.Activity,
    private val params: List<Parameter>,
    vararg parameterToPrefix: Pair<ParameterSpec, CodeBlock>
) {

    private val parameterToPrefix = parameterToPrefix.takeUnless { it.isEmpty() } ?: arrayOf(null to null)

    private val functionName = "bundleFor${receiver.name.simpleName}"

    fun addTo(fileSpec: FileSpec.Builder) {
        for ((parameter, prefix) in parameterToPrefix) {
            fileSpec.addFunction(functionName) {
                returns(bundleTypeName)
                if (parameter != null) {
                    addParameter(parameter)
                }
                addParameters(params.asParameterSpecs())
                addStatement("return %L", params.asBundleOf(prefix))
            }
        }
    }

    fun getCallBlock(prefix: CodeBlock? = null): CodeBlock = with(params) {
        when {
            isEmpty() && prefix == null -> CodeBlock.of("%L()", functionName)
            isEmpty() -> CodeBlock.of("%L(%L)", functionName, prefix)
            else -> buildCodeBlock {
                add("%L(⇥\n", functionName)
                if (prefix != null) {
                    add("%L,\n", prefix)
                }
                for ((index, param) in withIndex()) {
                    add("%L", param.name)
                    if (index + 1 != size) add(",\n")
                }
                add("\n⇤)")
            }
        }
    }
}
