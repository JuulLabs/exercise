package com.juullabs.exercise.compile.generator.code

import com.juullabs.exercise.compile.Parameters
import com.juullabs.exercise.compile.addClass
import com.juullabs.exercise.compile.addConstructor
import com.juullabs.exercise.compile.asBundleOf
import com.juullabs.exercise.compile.asParameterSpecs
import com.juullabs.exercise.compile.contextTypeName
import com.juullabs.exercise.compile.intentTypeName
import com.juullabs.exercise.compile.stringTypeName
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec

internal class ParameterizedIntentClassCodeGenerator(
    private val targetClass: ClassName,
    private val params: Parameters
) : CodeGenerator {

    override fun addTo(fileSpec: FileSpec.Builder) {
        val className = ClassName(fileSpec.packageName, "${targetClass.simpleName}Intent")
        fileSpec.addClass(className) {
            superclass(intentTypeName)
            addIntentConstructor(className, "context", contextTypeName, CodeBlock.of("context.packageName"))
            addIntentConstructor(className, "packageName", stringTypeName, CodeBlock.of("packageName"))
        }
    }

    private fun TypeSpec.Builder.addIntentConstructor(
        className: ClassName,
        packageNameArgument: String,
        packageNameArgumentType: TypeName,
        argumentToPackageName: CodeBlock
    ) = addConstructor {
        callSuperConstructor()
        addParameter(packageNameArgument, packageNameArgumentType)
        addParameters(params.asParameterSpecs())
        addStatement("setClassName(%L, %S)", packageNameArgument, className)
        if (params.all.isNotEmpty()) {
            addCode("replaceExtras(%L)\n", params.asBundleOf(argumentToPackageName))
        }
    }
}
