package com.juullabs.exercise.compile.generator.file

import com.juullabs.exercise.annotations.FromStub
import com.juullabs.exercise.annotations.ResultContract
import com.juullabs.exercise.compile.Parameters
import com.juullabs.exercise.compile.build
import com.juullabs.exercise.compile.generator.code.GetExtrasClassCodeGenerator
import com.juullabs.exercise.compile.generator.code.ResultSugarFunctionsCodeGenerator
import com.juullabs.exercise.compile.generator.code.addFrom
import com.juullabs.exercise.compile.get
import com.juullabs.exercise.compile.getAnnotation
import com.juullabs.exercise.compile.getResultKinds
import com.juullabs.exercise.compile.hasAnnotation
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
            addFrom(GetExtrasClassCodeGenerator(targetClass, parameters))

            val resultContractMirror = element.getAnnotation<ResultContract>()
            if (resultContractMirror != null) {
                val kinds = getResultKinds(resultContractMirror)
                addFrom(ResultSugarFunctionsCodeGenerator(targetClass, kinds))
            }
        }
    }
}

