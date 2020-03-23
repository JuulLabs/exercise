package com.juullabs.exercise.compile

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.ParameterSpec
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.TypeElement

internal operator fun AnnotationMirror.get(key: String): Any? =
    elementValues.entries.singleOrNull { (k, _) -> k.simpleName.toString() == key }?.value?.value

internal fun List<Parameter>.toBundle(
    prefix: String? = null
): CodeBlock = if (isEmpty()) {
    CodeBlock.of("bundleOf()")
} else {
    CodeBlock.of(
        "bundleOf(⇥\n%L\n⇤)",
        joinToString(",\n") { "\"${prefix ?: ""}${it.name}\" to ${it.name}" }
    )
}

internal fun List<Parameter>.asParameterSpecs(): List<ParameterSpec> = sequence {
    for (arg in this@asParameterSpecs.sortedBy { it.optional }) {
        val parameterBuilder = ParameterSpec.builder(arg.name, arg.combinedTypeName)
        if (arg.optional) {
            parameterBuilder.defaultValue("null")
        }
        yield(parameterBuilder.build())
    }
}.toList()

internal inline fun <reified T : Annotation> TypeElement.getAnnotationMirror(): AnnotationMirror? =
    annotationMirrors.asSequence().filter { ty ->
        val name = (ty.annotationType.asElement() as TypeElement).qualifiedName.toString()
        name in setOf(T::class).map { it.java.canonicalName }
    }.singleOrNull()
