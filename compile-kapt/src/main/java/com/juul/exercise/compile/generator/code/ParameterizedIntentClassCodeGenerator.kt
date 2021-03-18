package com.juul.exercise.compile.generator.code

import com.juul.exercise.compile.Parameters
import com.juul.exercise.compile.addClass
import com.juul.exercise.compile.addConstructor
import com.juul.exercise.compile.asParameterSpecs
import com.juul.exercise.compile.contextTypeName
import com.juul.exercise.compile.intentTypeName
import com.juul.exercise.compile.stringTypeName
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import javax.lang.model.element.Element

internal class ParameterizedIntentClassCodeGenerator(
    private val originatingElement: Element,
    private val targetClass: ClassName,
    private val params: Parameters
) : CodeGenerator {

    private val requiredParameterOptions = arrayOf(
        ParameterSpec("context", contextTypeName) to CodeBlock.of("context.packageName"),
        ParameterSpec("packageName", stringTypeName) to CodeBlock.of("packageName")
    )

    private val bundleCodeGenerator = BundleCodeGenerator(
        originatingElement, targetClass, params, *requiredParameterOptions
    )

    override fun addTo(fileSpec: FileSpec.Builder) {
        bundleCodeGenerator.addTo(fileSpec)
        val className = ClassName(fileSpec.packageName, "${targetClass.simpleName}Intent")
        fileSpec.addClass(className) {
            originatingElements += originatingElement
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
        addStatement("setClassName(%L, %S)", packageNameArgument, targetClass)
        if (params.all.isNotEmpty()) {
            addCode("replaceExtras(%L)\n", bundleCodeGenerator.getCallBlock(argumentToPackageName))
        }
    }
}
