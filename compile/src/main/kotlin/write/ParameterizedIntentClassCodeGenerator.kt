package com.juul.exercise.compile.write

import com.juul.exercise.compile.addClass
import com.juul.exercise.compile.addConstructor
import com.juul.exercise.compile.contextTypeName
import com.juul.exercise.compile.data.Parameter
import com.juul.exercise.compile.data.Receiver
import com.juul.exercise.compile.intentTypeName
import com.juul.exercise.compile.stringTypeName
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec

internal class ParameterizedIntentClassCodeGenerator(
    private val receiver: Receiver,
    private val params: List<Parameter>
) {

    private val requiredParameterOptions = arrayOf(
        ParameterSpec("context", contextTypeName) to CodeBlock.of("context.packageName"),
        ParameterSpec("packageName", stringTypeName) to CodeBlock.of("packageName")
    )

    private val bundleCodeGenerator = BundleCodeGenerator(
        receiver, params, *requiredParameterOptions
    )

    fun addTo(fileSpec: FileSpec.Builder) {
        bundleCodeGenerator.addTo(fileSpec)
        val className = ClassName(fileSpec.packageName, "${receiver.name.simpleName}Intent")
        fileSpec.addClass(className) {
            superclass(intentTypeName)
            for ((parameter, prefix) in requiredParameterOptions) {
                addIntentConstructor(parameter.name, parameter.type, prefix)
            }
        }
    }

    private fun TypeSpec.Builder.addIntentConstructor(
        packageNameArgument: String,
        packageNameArgumentType: TypeName,
        argumentToPackageName: CodeBlock
    ) = addConstructor {
        callSuperConstructor()
        addParameter(packageNameArgument, packageNameArgumentType)
        addParameters(params.asParameterSpecs())
        addStatement("setClassName(%L, %S)", packageNameArgument, receiver.name)
        if (params.isNotEmpty()) {
            addCode("replaceExtras(%L)\n", bundleCodeGenerator.getCallBlock(argumentToPackageName))
        }
    }
}
