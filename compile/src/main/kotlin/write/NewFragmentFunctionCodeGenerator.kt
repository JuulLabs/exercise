package com.juul.exercise.compile.write

import com.juul.exercise.compile.addFunction
import com.juul.exercise.compile.data.Parameter
import com.juul.exercise.compile.data.Receiver
import com.squareup.kotlinpoet.FileSpec

internal class NewFragmentFunctionCodeGenerator(
    private val receiver: Receiver.Fragment,
    private val params: List<Parameter>,
) {

    private val bundleCodeGenerator = BundleCodeGenerator(receiver, params)

    fun addTo(fileSpec: FileSpec.Builder) {
        bundleCodeGenerator.addTo(fileSpec)
        fileSpec.addFunction("new${receiver.name.simpleName}") {
            returns(receiver.name)
            addParameters(params.asParameterSpecs())
            addStatement("val instance = %T()", receiver.name)
            addCode("instance.arguments = %L\n", bundleCodeGenerator.getCallBlock())
            addStatement("return instance")
        }
    }
}
