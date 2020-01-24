package com.juullabs.exercise.compile

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import javax.annotation.processing.Filer
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement

internal abstract class CodeGenerator(
    environment: ProcessingEnvironment,
    type: TypeElement
) {
    protected val parameters: Parameters = Parameters(environment, type)

    protected val typeClassName: ClassName = type.asClassName()

    protected val typeName: String = type.simpleName.toString()

    private val typePackage: String =
        environment.elementUtils.getPackageOf(type).qualifiedName.toString()

    private val typeIsAbstract: Boolean = type.modifiers.contains(Modifier.ABSTRACT)

    private val fileSpecBuilder: FileSpec.Builder =
        FileSpec.builder(typePackage, "${typeName}Exercise")

    private var fileSpec: FileSpec? = null

    private val filer: Filer = environment.filer

    /** Name of the generated class. */
    protected abstract val exerciseClassName: String
    /** Name of the generated extension function. */
    protected abstract val exerciseSugarName: String
    /** Code snippet for generated class to get stored value. */
    protected abstract val codeToGetFromBundle: String

    /** Function creates an `Intent` for activities or an instance of a `Fragment` w/ args set. */
    protected abstract fun generateBuilderFunction(): FunSpec

    /** Helper function for [generateBuilderFunction]. */
    protected fun FunSpec.Builder.addExerciseParameters() = this.apply {
        for (arg in this@CodeGenerator.parameters.all.sortedBy { it.optional }) {
            val parameterBuilder = ParameterSpec.builder(arg.name, arg.combinedTypeName)
            if (arg.optional) {
                parameterBuilder.defaultValue("null")
            }
            addParameter(parameterBuilder.build())
        }
    }

    /** Creates class which gets stored extras or arguments. */
    private fun generateParameterClass(): TypeSpec {
        val classBuilder = run {
            val constructorBuilder = FunSpec.constructorBuilder()
                .addParameter("instance", typeClassName)
            val propertyBuilder = PropertySpec.builder("instance", typeClassName)
                .addModifiers(KModifier.PRIVATE)
                .initializer("instance")
            TypeSpec.classBuilder(exerciseClassName)
                .addModifiers(KModifier.INTERNAL)
                .primaryConstructor(constructorBuilder.build())
                .addProperty(propertyBuilder.build())
        }
        for (arg in parameters.all) {
            val propertyBuilder = run {
                val getterBuilder = FunSpec.getterBuilder()
                    .beginControlFlow("return with (instance) {")
                    .addStatement(
                        "$codeToGetFromBundle as %L",
                        arg.name,
                        arg.combinedTypeName
                    ).endControlFlow()
                PropertySpec.builder(arg.name, arg.combinedTypeName)
                    .getter(getterBuilder.build())
            }
            classBuilder.addProperty(propertyBuilder.build())
            if (arg.optional) {
                val functionBuilder = FunSpec.builder(arg.name)
                    .returns(arg.nonNullTypeName)
                    .addParameter("default", arg.nonNullTypeName)
                    .beginControlFlow("return with (instance) {")
                    .addStatement(
                        "($codeToGetFromBundle as? %L) ?: default",
                        arg.name,
                        arg.nonNullTypeName
                    ).endControlFlow()
                classBuilder.addFunction(functionBuilder.build())
            }
        }
        return classBuilder.build()
    }

    private fun generateParameterSugar(): PropertySpec {
        val getterBuilder = FunSpec.getterBuilder()
            .addStatement("return $exerciseClassName(this)")
        val extensionBuilder = PropertySpec.builder(exerciseSugarName, ClassName(typePackage, exerciseClassName))
                .addModifiers(KModifier.INTERNAL)
                .receiver(typeClassName)
                .getter(getterBuilder.build())
        return extensionBuilder.build()
    }

    /** Call at-most once per instance. */
    fun build() {
        check(fileSpec == null)
        fileSpecBuilder.addType(generateParameterClass())
        fileSpecBuilder.addProperty(generateParameterSugar())
        if (!typeIsAbstract) {
            fileSpecBuilder.addImport("androidx.core.os", "bundleOf")
            fileSpecBuilder.addFunction(generateBuilderFunction())
        }
        fileSpec = fileSpecBuilder.build()
    }

    /** Must be called after [build] */
    fun write(): Unit = checkNotNull(fileSpec).writeTo(filer)
}
