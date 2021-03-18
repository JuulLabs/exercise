package com.juul.exercise.compile.generator.code

import com.squareup.kotlinpoet.FileSpec

internal interface CodeGenerator {
    fun addTo(fileSpec: FileSpec.Builder)
}

internal fun FileSpec.Builder.addFrom(generator: CodeGenerator) = generator.addTo(this)
