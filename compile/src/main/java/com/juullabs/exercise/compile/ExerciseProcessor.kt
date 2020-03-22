package com.juullabs.exercise.compile

import com.juullabs.exercise.annotations.Exercise
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedOptions
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("com.juullabs.exercise.annotations.Exercise")
@SupportedOptions(OPTION_PACKAGE_NAME)
class ExerciseProcessor : AbstractProcessor() {

    private fun TypeElement.isSubtypeOf(supertype: String): Boolean = with(processingEnv) {
        val thisMirror = typeUtils.getDeclaredType(this@isSubtypeOf)
        val supertypeMirror = typeUtils.getDeclaredType(elementUtils.getTypeElement(supertype))
        typeUtils.isAssignable(thisMirror, supertypeMirror)
    }

    private fun TypeElement.isSubtypeOfAny(vararg supertypes: String): Boolean =
        supertypes.any { this.isSubtypeOf(it) }

    override fun process(
        annotations: Set<TypeElement>,
        roundEnvironment: RoundEnvironment
    ): Boolean {
        for (type in roundEnvironment.getElementsAnnotatedWith(Exercise::class.java)) {
            val codeGenerator = when {
                type !is TypeElement -> null
                type.isSubtypeOf("android.app.Activity") ->
                    ActivityCodeGenerator(processingEnv, type)
                type.isSubtypeOfAny("android.app.Fragment", "androidx.fragment.app.Fragment") ->
                    FragmentCodeGenerator(processingEnv, type)
                else -> null
            }
            if (codeGenerator != null) {
                codeGenerator.build()
                codeGenerator.write()
            } else {
                // TODO: Warn users for unsupported types
            }
        }
        return true
    }
}
