package com.juullabs.exercise.compile

import com.juullabs.exercise.annotations.Exercise
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("com.juullabs.exercise.annotations.Exercise")
class ExerciseProcessor : AbstractProcessor() {

    override fun process(
        annotations: Set<TypeElement>,
        roundEnvironment: RoundEnvironment
    ): Boolean {
        for (activity in roundEnvironment.getElementsAnnotatedWith(Exercise::class.java)) {
            if (activity !is TypeElement) continue
            // TODO: Actually check that `activity` is an activity.
            CodeGenerator(processingEnv, activity)
                .apply { build() }
                .write()
        }
        return true
    }
}
