package com.juullabs.exercise.compile

import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.TypeElement

internal class Extras(
    environment: ProcessingEnvironment,
    typeElement: TypeElement
) {
    val direct: List<Extra> by lazy {
        @Suppress("UNCHECKED_CAST")
        typeElement.annotationMirrors.asSequence()
            .filter {
                val name = (it.annotationType.asElement() as TypeElement).qualifiedName.toString()
                name == com.juullabs.exercise.annotations.Exercise::class.java.canonicalName
            }
            .singleOrNull()
            ?.elementValues
            ?.values
            ?.single()
            ?.value
            ?.let { it as List<AnnotationMirror> }
            ?.map { Extra.fromAnnotation(it) }
            .orEmpty()
    }

    val indirect: List<Extra> by lazy {
        val parentElement = environment.typeUtils.asElement(typeElement.superclass) as? TypeElement
        if (parentElement != null) {
            val parent = Extras(environment, parentElement)
            parent.all
        } else emptyList()
    }

    val all: List<Extra> by lazy {
        indirect + direct
    }
}
