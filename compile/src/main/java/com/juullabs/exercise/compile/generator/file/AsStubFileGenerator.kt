package com.juullabs.exercise.compile.generator.file

import com.juullabs.exercise.annotations.AsStub
import com.juullabs.exercise.annotations.ResultContract
import com.juullabs.exercise.compile.Parameters
import com.juullabs.exercise.compile.build
import com.juullabs.exercise.compile.generator.code.ParameterizedIntentClassCodeGenerator
import com.juullabs.exercise.compile.generator.code.ResultClassCodeGenerator
import com.juullabs.exercise.compile.generator.code.ResultContractClassCodeGenerator
import com.juullabs.exercise.compile.generator.code.addFrom
import com.juullabs.exercise.compile.get
import com.juullabs.exercise.compile.getAnnotation
import com.juullabs.exercise.compile.getResultKinds
import com.juullabs.exercise.compile.hasAnnotation
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

internal class AsStubFileGenerator(
    private val environment: ProcessingEnvironment
) : FileGenerator {
    override fun isGeneratorFor(element: Element): Boolean = element is TypeElement &&
        element.hasAnnotation<AsStub>()

    override fun generate(element: Element): FileSpec {
        check(element is TypeElement)
        val parameters = Parameters(environment, element)
        val annotation = checkNotNull(element.getAnnotation<AsStub>())
        val targetClass = ClassName(annotation["packageName"] as String, annotation["className"] as String)
        return FileSpec.build(targetClass.packageName, "${targetClass.simpleName}ExerciseStubs") {
            addFrom(ParameterizedIntentClassCodeGenerator(element, targetClass, parameters))

            val resultContractMirror = element.getAnnotation<ResultContract>()
            if (resultContractMirror != null) {
                val kinds = getResultKinds(resultContractMirror)
                addFrom(ResultContractClassCodeGenerator(element, targetClass, kinds))
                addFrom(ResultClassCodeGenerator(element, targetClass, kinds))
            }
        }
    }
}
