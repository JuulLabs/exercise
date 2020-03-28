package com.juullabs.exercise.compile.generator.file

import com.squareup.kotlinpoet.FileSpec
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element

internal interface FileGenerator {
    fun isGeneratorFor(element: Element): Boolean
    fun generate(element: Element): FileSpec
}

private fun generators(environment: ProcessingEnvironment) = sequence {
    yield(AsStubFileGenerator(environment))
    yield(FromStubFileGenerator(environment))
    yield(ActivityFileGenerator(environment))
    yield(FragmentFileGenerator(environment))
}

internal fun Element.getCodeGenerator(environment: ProcessingEnvironment): FileGenerator? =
    generators(environment).firstOrNull { it.isGeneratorFor(this) }
