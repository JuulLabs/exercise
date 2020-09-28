package com.juul.exercise.compile.generator.code

import com.juul.exercise.compile.Parameters
import com.juul.exercise.compile.addFunction
import com.juul.exercise.compile.asParameterSpecs
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import javax.lang.model.element.Element

internal class NewFragmentFunctionCodeGenerator(
    private val originatingElement: Element,
    private val targetClass: ClassName,
    private val params: Parameters
) : CodeGenerator {

    private val bundleCodeGenerator = BundleCodeGenerator(originatingElement, targetClass, params)

    override fun addTo(fileSpec: FileSpec.Builder) {
        bundleCodeGenerator.addTo(fileSpec)
        fileSpec.addFunction("new${targetClass.simpleName}") {
            originatingElements += originatingElement
            returns(targetClass)
            addParameters(params.asParameterSpecs())
            addStatement("val instance = %T()", targetClass)
            addCode("instance.arguments = %L\n", bundleCodeGenerator.getCallBlock())
            addStatement("return instance")
        }
    }
}
