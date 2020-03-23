package com.juullabs.exercise.compile

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.TypeElement

internal class FragmentCodeGenerator(
    environment: ProcessingEnvironment,
    type: TypeElement
) : CodeGenerator(environment, type) {

    override val exerciseClassName: String = "${typeName}Arguments"
    override val exerciseSugarName: String = "args"
    override val codeToRetrieveFromInstance: String = "%N.arguments?.get(%S)"

    override fun onBuild(fileSpecBuilder: FileSpec.Builder) {
        if (!typeIsAbstract) {
            fileSpecBuilder.addImport("androidx.core.os", "bundleOf")
            fileSpecBuilder.addFunction(generateBuilder())
        }
    }

    private fun generateBuilder(): FunSpec {
        val arguments = parameters.all
        return FunSpec.build("new$typeName") {
            returns(typeClassName)
            if (arguments.isEmpty()) {
                addStatement("return $typeName()")
            } else {
                addParameters(arguments.asParameterSpecs())
                addStatement("val instance = $typeName()")
                beginControlFlow("return instance.apply")
                addCode("arguments = %L\n", arguments.toBundle())
                endControlFlow()
            }
        }
    }
}
