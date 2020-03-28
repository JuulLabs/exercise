package com.juullabs.exercise.compile

import com.juullabs.exercise.annotations.Exercise
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.ParameterSpec
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.TypeElement

internal class Parameters(
    environment: ProcessingEnvironment,
    typeElement: TypeElement
) {
    private val direct: List<Parameter> by lazy {
        @Suppress("UNCHECKED_CAST")
        typeElement.getAnnotation<Exercise>()
            ?.get("params")
            ?.let { it as List<AnnotationMirror> }
            ?.map { Parameter.fromAnnotation(it) }
            .orEmpty()
    }

    private val indirect: List<Parameter> by lazy {
        when (val parent = environment.typeUtils.asElement(typeElement.superclass) as? TypeElement) {
            null -> emptyList()
            else -> Parameters(environment, parent).all
        }
    }

    val all: List<Parameter> by lazy { indirect + direct }
}

internal fun Parameters.asBundleOf(prefix: CodeBlock? = null): CodeBlock = all.asBundleOf(prefix)

internal fun List<Parameter>.asBundleOf(
    prefix: CodeBlock? = null
): CodeBlock = when {
    isEmpty() -> CodeBlock.of("%M()", bundleOfMemberName)
    prefix == null -> CodeBlock.of(
        "%M(⇥\n%L\n⇤)",
        bundleOfMemberName,
        joinToString(",\n") { "\"${it.name}\" to ${it.name}" }
    )
    else -> CodeBlock.of(
        "%M(⇥\n%L\n⇤)",
        bundleOfMemberName,
        joinToString(",\n") { "\"\${$prefix}.${it.name}\" to ${it.name}" }
    )
}

internal fun Parameters.asParameterSpecs(): List<ParameterSpec> = all.asParameterSpecs()

@OptIn(ExperimentalStdlibApi::class)
internal fun List<Parameter>.asParameterSpecs(): List<ParameterSpec> = asSequence()
    .sortedBy { it.optional }
    .map { arg ->
        ParameterSpec.build(arg.name, arg.combinedTypeName) {
            if (arg.optional) {
                defaultValue("null")
            }
        }
    }.toList()
