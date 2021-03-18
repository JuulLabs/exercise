package com.juul.exercise.compile.generator.code

import com.juul.exercise.compile.Parameters
import com.juul.exercise.compile.addFunction
import com.juul.exercise.compile.asBundleOf
import com.juul.exercise.compile.asParameterSpecs
import com.juul.exercise.compile.bundleTypeName
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.buildCodeBlock
import javax.lang.model.element.Element

internal class BundleCodeGenerator(
    private val originatingElement: Element,
    private val targetClass: ClassName,
    private val params: Parameters,
    vararg parameterToPrefix: Pair<ParameterSpec, CodeBlock>
) : CodeGenerator {

    private val parameterToPrefix = parameterToPrefix.takeUnless { it.isEmpty() } ?: arrayOf(null to null)

    private val functionName = "bundleFor${targetClass.simpleName}"

    override fun addTo(fileSpec: FileSpec.Builder) {
        for ((parameter, prefix) in parameterToPrefix) {
            fileSpec.addFunction(functionName) {
                originatingElements += originatingElement
                returns(bundleTypeName)
                if (parameter != null) {
                    addParameter(parameter)
                }
                addParameters(params.asParameterSpecs())
                addStatement("return %L", params.asBundleOf(prefix))
            }
        }
    }

    fun getCallBlock(prefix: CodeBlock? = null): CodeBlock = with(this.params.all) {
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
