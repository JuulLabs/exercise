package com.juul.exercise.compile.read

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSType
import com.juul.exercise.annotations.AsStub
import com.juul.exercise.annotations.FromStub

private val SOURCE = FromStub::source.name

internal fun KSClassDeclaration.findDependencies(): Dependencies {
    @OptIn(ExperimentalStdlibApi::class)
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
