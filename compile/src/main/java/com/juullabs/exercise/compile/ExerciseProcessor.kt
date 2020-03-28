package com.juullabs.exercise.compile

import com.juullabs.exercise.annotations.Exercise
import com.juullabs.exercise.compile.generator.file.getCodeGenerator
import java.io.PrintWriter
import java.io.StringWriter
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("com.juullabs.exercise.annotations.Exercise")
class ExerciseProcessor : AbstractProcessor() {

    override fun process(
        annotations: Set<TypeElement>,
        roundEnvironment: RoundEnvironment
    ): Boolean {
        for (element in roundEnvironment.getElementsAnnotatedWith(Exercise::class.java)) {
            try {
                element.getCodeGenerator(processingEnv)
                    ?.generate(element)
                    ?.writeTo(processingEnv.filer)
                    ?: error("No code generator found for element ${element.simpleName}.")
            } catch (e: RuntimeException) {
                val stackTrace = StringWriter().apply { PrintWriter(this).run(e::printStackTrace) }
                val message = "An error occurred during code generation. Caused by:\n$stackTrace"
                val annotation = checkNotNull(element.getAnnotation<Exercise>())
                processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, message, element, annotation)
            }
        }
        return true
    }
}
