package com.juul.exercise.compile.generator.file

import com.juul.exercise.annotations.Exercise
import com.juul.exercise.annotations.FromStub
import com.juul.exercise.annotations.ResultContract
import com.juul.exercise.compile.Parameters
import com.juul.exercise.compile.build
import com.juul.exercise.compile.generator.code.GetExtrasClassCodeGenerator
import com.juul.exercise.compile.generator.code.ResultSugarFunctionsCodeGenerator
import com.juul.exercise.compile.generator.code.addFrom
import com.juul.exercise.compile.get
import com.juul.exercise.compile.getAnnotation
import com.juul.exercise.compile.getResultKinds
import com.juul.exercise.compile.hasAnnotation
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.asClassName
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror

internal class FromStubFileGenerator(
    private val environment: ProcessingEnvironment
) : FileGenerator {
    override fun isGeneratorFor(element: Element): Boolean = element is TypeElement &&
        element.hasAnnotation<FromStub>()

    override fun generate(element: Element): FileSpec {
        check(element is TypeElement)
        val annotation = checkNotNull(element.getAnnotation<FromStub>())
        val source = environment.typeUtils.asElement(annotation["source"] as TypeMirror) as TypeElement
        val parameters = Parameters(environment, source)
        val targetClass = element.asClassName()
        return FileSpec.build(targetClass.packageName, "${targetClass.simpleName}Exercise") {
            addFrom(GetExtrasClassCodeGenerator(element, targetClass, parameters))

            val resultContractMirror = source.getAnnotation<ResultContract>()
            if (resultContractMirror != null) {
                val kinds = getResultKinds(resultContractMirror)
                addFrom(ResultSugarFunctionsCodeGenerator(element, targetClass, kinds))
            }
        }
    }
}

