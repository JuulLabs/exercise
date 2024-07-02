package com.juul.exercise.compile.read

import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName

internal inline fun <reified T : Annotation> KSAnnotated.getAnnotation(): KSAnnotation? =
    annotations.singleOrNull {
        it.annotationType
            .resolve()
            .declaration.qualifiedName
            ?.asString() == T::class.qualifiedName
    }

internal fun KSAnnotation.getArgument(name: String): Any? =
    arguments.firstOrNull { it.name?.asString() == name }?.value

internal val KSClassDeclaration.superClass: KSClassDeclaration?
    get() = superTypes
        .asSequence()
        .map { it.resolve().declaration }
        .filterIsInstance<KSClassDeclaration>()
        .singleOrNull { it.classKind == ClassKind.CLASS }

internal fun KSClassDeclaration.toClassName() = ClassName(packageName.asString(), simpleName.asString())
