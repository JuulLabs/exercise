package com.juullabs.exercise.compile

import com.squareup.kotlinpoet.asTypeName
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

internal inline fun <reified T : Annotation> Element.hasAnnotation(): Boolean =
    getAnnotation<T>() != null

internal inline fun <reified T : Annotation> Element.getAnnotation(): AnnotationMirror? {
    return annotationMirrors.asSequence().filter { ty ->
        val name = (ty.annotationType.asElement() as TypeElement).qualifiedName.toString()
        name == T::class.java.canonicalName
    }.singleOrNull()
}

internal fun TypeElement.isSubtypeOf(env: ProcessingEnvironment, supertype: String): Boolean {
    val thisMirror = env.typeUtils.getDeclaredType(this)
    val supertypeMirror = env.typeUtils.getDeclaredType(env.elementUtils.getTypeElement(supertype))
    return env.typeUtils.isAssignable(thisMirror, supertypeMirror)
}

internal fun TypeElement.isSubtypeOfAny(env: ProcessingEnvironment, vararg supertypes: String): Boolean =
    supertypes.any { isSubtypeOf(env, it) }
