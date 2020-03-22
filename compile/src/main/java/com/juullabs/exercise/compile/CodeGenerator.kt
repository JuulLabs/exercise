package com.juullabs.exercise.compile

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import javax.annotation.processing.Filer
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement

internal abstract class CodeGenerator(
    protected val environment: ProcessingEnvironment,
    protected val type: TypeElement
) {
    protected val parameters: Parameters = Parameters(environment, type)

    protected val typeClassName: ClassName = type.asClassName()

    protected val typeName: String = type.simpleName.toString()

    protected val typeIsAbstract: Boolean = type.modifiers.contains(Modifier.ABSTRACT)

    protected val typePackage: String =
        environment.elementUtils.getPackageOf(type).qualifiedName.toString()

    private val fileSpecBuilder: FileSpec.Builder =
        FileSpec.builder(typePackage, "${typeName}Exercise")

    private var fileSpec: FileSpec? = null

    private val filer: Filer = environment.filer

    /** Name of the generated class. */
    protected abstract val exerciseClassName: String

    /** Name of the generated extension function. */
    protected abstract val exerciseSugarName: String

    /** Code snippet for generated class to get stored value. */
    protected abstract val codeToRetrieveFromInstance: String

    protected abstract fun onBuild(fileSpecBuilder: FileSpec.Builder)

    protected fun TypeSpec.Builder.addExerciseParameter(param: Parameter, codeSnippet: String, instance: String) {
        addProperty(param.name, param.combinedTypeName) {
            getter {
                if (param.isParameterized) {
                    addStatement("@Suppress(\"UNCHECKED_CAST\")")
                }
                addStatement("return $codeSnippet as %T", instance, param.name, param.combinedTypeName)
            }
        }
        if (param.optional) {
            addFunction(param.name) {
                returns(param.nonNullTypeName)
                addParameter("default", param.nonNullTypeName)
                if (param.isParameterized) {
                    addStatement("@Suppress(\"UNCHECKED_CAST\")")
                }
                addStatement("return ($codeSnippet as? %T) ?: default", instance, param.name, param.nonNullTypeName)
            }
        }
    }

    /** Creates class which gets stored extras or arguments. */
    private fun generateParameterClass(): TypeSpec = TypeSpec.buildClass(exerciseClassName) {
        addModifiers(KModifier.INTERNAL)
        primaryConstructor { addParameter("instance", typeClassName) }
        addProperty("instance", typeClassName, KModifier.PRIVATE) { initializer("instance") }
        for (param in parameters.all) {
            addExerciseParameter(param, codeToRetrieveFromInstance, "instance")
        }
    }

    private fun generateParameterSugar() = PropertySpec.build(
        name = exerciseSugarName,
        type = ClassName(typePackage, exerciseClassName)
    ) {
        addModifiers(KModifier.INTERNAL)
        receiver(typeClassName)
        getter { addStatement("return $exerciseClassName(this)") }
    }

    /** Call at-most once per instance. */
    fun build() {
        check(fileSpec == null)
        fileSpecBuilder.indent("    ")
        onBuild(fileSpecBuilder)
        if (parameters.all.isNotEmpty()) {
            fileSpecBuilder.addType(generateParameterClass())
            fileSpecBuilder.addProperty(generateParameterSugar())
        }
        fileSpec = fileSpecBuilder.build()
    }

    /** Must be called after [build] */
    fun write(): Unit = checkNotNull(fileSpec).writeTo(filer)
}
