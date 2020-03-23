package com.juullabs.exercise.compile

import com.juullabs.exercise.annotations.Exercise
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.TypeElement

internal class Parameters(
    environment: ProcessingEnvironment,
    typeElement: TypeElement
) {
    private val direct: List<Parameter> by lazy {
        @Suppress("UNCHECKED_CAST")
        typeElement.getAnnotationMirror<Exercise>()
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

    val all: List<Parameter> by lazy {
        indirect + direct
    }
}
