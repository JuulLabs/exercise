package com.juul.exercise.compile.read

import android.app.Activity
import android.app.Service
import androidx.fragment.app.Fragment
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier.ABSTRACT
import com.google.devtools.ksp.symbol.Modifier.SEALED
import com.juul.exercise.annotations.AsStub
import com.juul.exercise.annotations.FromStub
import com.juul.exercise.compile.data.Receiver
import com.squareup.kotlinpoet.ClassName

private val PACKAGE_NAME = AsStub::packageName.name
private val CLASS_NAME = AsStub::className.name

internal fun KSClassDeclaration.asReceiver(): Receiver {
    val superClasses = generateSequence(superClass) { it.superClass }
        .map { it.qualifiedName?.asString() }
        .toSet()

    val isAbstract = ABSTRACT in modifiers || SEALED in modifiers
    val fromStubAnnotation = getAnnotation<FromStub>()
    val asStubAnnotation = getAnnotation<AsStub>()

    return when {
        Activity::class.qualifiedName in superClasses ->
            Receiver.Activity(this.toClassName(), fromStubAnnotation != null, isAbstract)
        Fragment::class.qualifiedName in superClasses ->
            Receiver.Fragment(this.toClassName(), isAbstract)
        Service::class.qualifiedName in superClasses ->
            Receiver.Service(this.toClassName(), isAbstract)
        asStubAnnotation != null -> {
            val packageName = asStubAnnotation.getArgument(PACKAGE_NAME) as String
            val className = asStubAnnotation.getArgument(CLASS_NAME) as String
            Receiver.Stub(ClassName(packageName, className))
        }
        else -> {
            val message = buildString {
                @Suppress("ktlint:standard:max-line-length", "ktlint:standard:argument-list-wrapping")
                append("@Exercise annotated class must be a subclass of Activity, Fragment, or Service; or, must also be annotated @AsStub. Found classes:")
                for (superClass in superClasses) {
                    append("\n  ", superClass)
                }
            }
            error(message)
        }
    }
}
