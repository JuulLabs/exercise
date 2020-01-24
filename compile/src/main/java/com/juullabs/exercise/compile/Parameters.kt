package com.juullabs.exercise.compile

import com.juullabs.exercise.annotations.Exercise
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.TypeElement

internal class Parameters(
    environment: ProcessingEnvironment,
    typeElement: TypeElement
) {
    val direct: List<Parameter> by lazy {
        @Suppress("UNCHECKED_CAST")
        typeElement.annotationMirrors.asSequence()
            .filter { ty ->
                val name = (ty.annotationType.asElement() as TypeElement).qualifiedName.toString()
                name in setOf(Exercise::class).map { it.java.canonicalName }
            }
            .singleOrNull()
            ?.elementValues
            ?.values
            ?.single()
            ?.value
            ?.let { it as List<AnnotationMirror> }
            ?.map { Parameter.fromAnnotation(it) }
            .orEmpty()
    }

    val indirect: List<Parameter> by lazy {
        val parentElement = environment.typeUtils.asElement(typeElement.superclass) as? TypeElement
        if (parentElement != null) {
            val parent = Parameters(environment, parentElement)
            parent.all
        } else emptyList()
    }

    val all: List<Parameter> by lazy {
        indirect + direct
    }
}
