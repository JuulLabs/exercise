package com.juullabs.exercise.compile

import com.juullabs.exercise.annotations.Exercise
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.buildCodeBlock
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
    else -> buildCodeBlock {
        add("%M(⇥\n", bundleOfMemberName)
        for ((index, param) in withIndex()) {
            val key = if (prefix == null) param.name else "\${$prefix}.${param.name}"
            val value = if (param.parceler == null) {
                CodeBlock.of(param.name)
            } else {
                CodeBlock.of("%T.%M(%L)", param.parceler, writeToParcelMemberName, param.name)
            }
            add("\"%L\" to %L", key, value)
            if (index + 1 != size) add(",\n")
        }
        add("\n⇤)")
    }
}

internal fun Parameters.asParameterSpecs(): List<ParameterSpec> = all.asParameterSpecs()

internal fun List<Parameter>.asParameterSpecs(): List<ParameterSpec> = asSequence()
    .sortedBy { it.optional }
    .map { arg ->
        ParameterSpec.build(arg.name, arg.combinedTypeName) {
            if (arg.optional) {
                defaultValue("null")
            }
        }
    }.toList()
