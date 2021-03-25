package com.juul.exercise.compile.read

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSType
import com.juul.exercise.annotations.AsStub
import com.juul.exercise.annotations.FromStub

private val SOURCE = FromStub::source.name

/**
 * Finds the [Dependencies] of a file generated for a given declaration. Our dependencies are the declared class,
 * all of it's superclasses, any stubs, and any superclasses of the stubs. Logically, we can think about this as the
 * set of classes that could be annotated with `@Exercise` to affect our parameter list.
 */
internal fun KSClassDeclaration.findDependencies(): Dependencies {
    fun KSClassDeclaration.findDependencies(): Sequence<KSFile?> = sequence {
        yield(containingFile)
        superClass?.let { parent ->
            yieldAll(parent.findDependencies())
        }
        val stub = getAnnotation<FromStub>()
            ?.getArgument(SOURCE)
            ?.let { it as KSType }
            ?.declaration
            ?.let { it as KSClassDeclaration }
        if (stub != null) {
            check(stub.getAnnotation<AsStub>() != null)
            yieldAll(stub.findDependencies())
        }
    }
    val dependencies = findDependencies()
        .filterNotNull()
        .toSet()
        .toTypedArray()
    return Dependencies(aggregating = false, *dependencies)
}
