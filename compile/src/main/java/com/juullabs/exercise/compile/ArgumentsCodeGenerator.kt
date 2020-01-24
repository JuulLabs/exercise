package com.juullabs.exercise.compile

import com.squareup.kotlinpoet.FunSpec
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.TypeElement

internal class ArgumentsCodeGenerator(
    environment: ProcessingEnvironment,
    type: TypeElement
) : CodeGenerator(environment, type) {

    override val exerciseClassName: String = "${typeName}Arguments"
    override val exerciseSugarName: String = "args"
    override val codeToGetFromBundle: String = "arguments?.get(%S)"

    override fun generateBuilderFunction(): FunSpec =
        FunSpec.builder("newInstanceOf$typeName")
            .returns(typeClassName)
            .addExerciseParameters()
            .addStatement("val instance = $typeName()")
            .beginControlFlow("return instance.apply")
            .addStatement(
                "arguments = bundleOf(%L)",
                parameters.all.joinToString { "\"${it.name}\" to ${it.name}" }
            ).endControlFlow()
            .build()
}
