package com.juullabs.exercise.compile.generator.code

import com.juullabs.exercise.compile.Parameters
import com.juullabs.exercise.compile.addFunction
import com.juullabs.exercise.compile.asBundleOf
import com.juullabs.exercise.compile.asParameterSpecs
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec

internal class NewFragmentFunctionCodeGenerator(
    private val targetClass: ClassName,
    private val params: Parameters
) : CodeGenerator {

    override fun addTo(fileSpec: FileSpec.Builder) {
        fileSpec.addFunction("new${targetClass.simpleName}") {
            returns(targetClass)
            addParameters(params.asParameterSpecs())
            addStatement("val instance = %T()", targetClass)
            beginControlFlow("return instance.apply")
            addCode("arguments = %L\n", params.asBundleOf())
            endControlFlow()
        }
    }
}
