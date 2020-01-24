package com.juullabs.exercise.compile

import com.squareup.kotlinpoet.FunSpec
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.TypeElement

internal class ExtrasCodeGenerator(
    environment: ProcessingEnvironment,
    type: TypeElement
) : CodeGenerator(environment, type) {

    override val exerciseClassName: String = "${typeName}Extras"
    override val exerciseSugarName: String = "extras"
    override val codeToGetFromBundle: String = "intent?.extras?.get(\"\$packageName.%L\")"

    override fun generateBuilderFunction(): FunSpec =
        FunSpec.builder("intentFor$typeName")
            .receiver(contextTypeName)
            .returns(intentTypeName)
            .addExerciseParameters()
            .addStatement("val intent = Intent(this, $typeName::class.java)")
            .beginControlFlow("return intent.apply")
            .addStatement(
                "replaceExtras(bundleOf(%L))",
                parameters.all.joinToString { "\"\$packageName.${it.name}\" to ${it.name}" }
            ).endControlFlow()
            .build()
}
