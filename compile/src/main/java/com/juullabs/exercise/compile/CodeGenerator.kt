package com.juullabs.exercise.compile

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement

class CodeGenerator(
    private val environment: ProcessingEnvironment,
    private val activity: TypeElement
) {
    private val activityName: String =
        activity.simpleName.toString()

    private val packageName: String =
        environment.elementUtils.getPackageOf(activity).qualifiedName.toString()

    private val fileName: String =
        "${activity.simpleName}Exercise"

    private val fileBuilder: FileSpec.Builder =
        FileSpec.builder(packageName, fileName)

    private val extras: Extras =
        Extras(environment, activity)

    private var fileSpec: FileSpec? = null

    fun build() {
        check(fileSpec == null)
        generateExtrasClass()
        generateExtrasClassSugar()
        if (!isAbstract()) {
            generateIntentBuilder()
        }
        fileSpec = fileBuilder.build()
    }

    fun write() {
        val fileSpec = checkNotNull(fileSpec)
        fileSpec.writeTo(environment.filer)
    }

    private fun isAbstract(): Boolean =
        activity.modifiers.contains(Modifier.ABSTRACT)

    private fun generateExtrasClass() {
        val classBuilder = run {
            val constructorBuilder = FunSpec.constructorBuilder()
                .addParameter("activity", activity.asClassName())
            val propertyBuilder = PropertySpec.builder("activity", activity.asClassName())
                .addModifiers(KModifier.PRIVATE)
                .initializer("activity")
            TypeSpec.classBuilder("${activityName}Extras")
                .addModifiers(KModifier.INTERNAL)
                .primaryConstructor(constructorBuilder.build())
                .addProperty(propertyBuilder.build())
        }
        for (extra in extras.all) {
            val propertyBuilder = run {
                val getterBuilder = FunSpec.getterBuilder()
                    .beginControlFlow("return with (activity) {")
                    .addStatement("intent?.extras?.get(packageName + \".${extra.name}\") as ${extra.combinedTypeName}")
                    .endControlFlow()
                PropertySpec.builder(extra.name, extra.combinedTypeName)
                    .getter(getterBuilder.build())
            }
            classBuilder.addProperty(propertyBuilder.build())
            if (extra.optional) {
                val functionBuilder = FunSpec.builder(extra.name)
                    .returns(extra.nonNullTypeName)
                    .addParameter("default", extra.nonNullTypeName)
                    .beginControlFlow("return with (activity) {")
                    .addStatement("(intent?.extras?.get(packageName + \".${extra.name}\") as? ${extra.nonNullTypeName}) ?: default")
                    .endControlFlow()
                classBuilder.addFunction(functionBuilder.build())
            }
        }
        fileBuilder.addType(classBuilder.build())
    }

    private fun generateExtrasClassSugar() {
        val getterBuilder = FunSpec.getterBuilder()
            .addStatement("return ${activityName}Extras(this)")
        val extensionBuilder = PropertySpec.builder("extras", ClassName(packageName, "${activityName}Extras"))
            .addModifiers(KModifier.INTERNAL)
            .receiver(activity.asClassName())
            .getter(getterBuilder.build())
        fileBuilder.addProperty(extensionBuilder.build())
    }

    private fun generateIntentBuilder() {
        check(fileSpec == null)
        check(!isAbstract())

        val functionBuilder = FunSpec.builder("intentFor$activityName")
            .receiver(contextTypeName)
            .returns(intentTypeName)
            .beginControlFlow("return Intent(this, $activityName::class.java).apply {")

        for (extra in extras.all.sortedBy { it.optional }) {
            val parameterBuilder = ParameterSpec.builder(extra.name, extra.combinedTypeName)
            if (extra.optional) {
                parameterBuilder.defaultValue("null")
            }
            functionBuilder.addParameter(parameterBuilder.build())
            if (extra.optional) {
                functionBuilder
                    .beginControlFlow("if (${extra.name} != null) {")
                    .addStatement("putExtra(packageName + \".${extra.name}\", checkNotNull(${extra.name}))")
                    .endControlFlow()
            } else {
                functionBuilder
                    .addStatement("putExtra(packageName + \".${extra.name}\", ${extra.name})")
            }
        }

        functionBuilder.endControlFlow()
        fileBuilder.addFunction(functionBuilder.build())
    }
}
