package com.juullabs.exercise.compile

import com.juullabs.exercise.annotations.Exercise
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
        try {
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
        } catch (e: RuntimeException) {
            val writer = StringWriter()
            e.printStackTrace(PrintWriter(writer))
            val stackTrace = writer.toString().replace("\n", "\\n")
            val message = "An error occurred during code generation. Caused by: $stackTrace"
            processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, message)
        }
        return true
    }
}
