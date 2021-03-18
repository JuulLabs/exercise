package com.juul.exercise.compile.read

import android.app.Activity
import android.app.Service
import androidx.fragment.app.Fragment
import com.google.devtools.ksp.symbol.KSClassDeclaration
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

    val fromStubAnnotation = getAnnotation<FromStub>()
    val asStubAnnotation = getAnnotation<AsStub>()

    return when {
        Activity::class.qualifiedName in superClasses ->
            Receiver.Activity(this.toClassName(), fromStubAnnotation != null)
        Fragment::class.qualifiedName in superClasses ->
            Receiver.Fragment(this.toClassName())
        Service::class.qualifiedName in superClasses ->
            Receiver.Service(this.toClassName())
        asStubAnnotation != null -> {
            val packageName = asStubAnnotation.getArgument(PACKAGE_NAME) as String
            val className = asStubAnnotation.getArgument(CLASS_NAME) as String
            Receiver.Stub(ClassName(packageName, className))
        }
        else -> throw IllegalArgumentException("@Exercise annotated class must be a subclass of Activity, Fragment, or Service; or, must also be annotated @AsStub.")
    }
}
