package com.juul.exercise.compile.generator.file

import com.juul.exercise.annotations.AsStub
import com.juul.exercise.annotations.ResultContract
import com.juul.exercise.compile.Parameters
import com.juul.exercise.compile.build
import com.juul.exercise.compile.generator.code.ParameterizedIntentClassCodeGenerator
import com.juul.exercise.compile.generator.code.ResultClassCodeGenerator
import com.juul.exercise.compile.generator.code.ResultContractClassCodeGenerator
import com.juul.exercise.compile.generator.code.addFrom
import com.juul.exercise.compile.get
import com.juul.exercise.compile.getAnnotation
import com.juul.exercise.compile.getResultKinds
import com.juul.exercise.compile.hasAnnotation
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
