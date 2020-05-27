package com.juullabs.exercise.compile.generator.file

import com.juullabs.exercise.annotations.ResultContract
import com.juullabs.exercise.compile.Parameters
import com.juullabs.exercise.compile.build
import com.juullabs.exercise.compile.generator.code.GetExtrasClassCodeGenerator
import com.juullabs.exercise.compile.generator.code.ParameterizedIntentClassCodeGenerator
import com.juullabs.exercise.compile.generator.code.ResultClassCodeGenerator
import com.juullabs.exercise.compile.generator.code.ResultContractClassCodeGenerator
import com.juullabs.exercise.compile.generator.code.ResultSugarFunctionsCodeGenerator
import com.juullabs.exercise.compile.generator.code.addFrom
import com.juullabs.exercise.compile.getAnnotation
import com.juullabs.exercise.compile.getResultKinds
import com.juullabs.exercise.compile.isSubtypeOf
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.asClassName
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement

internal class ActivityFileGenerator(
    private val environment: ProcessingEnvironment
) : FileGenerator {
    override fun isGeneratorFor(element: Element): Boolean = element is TypeElement &&
        element.isSubtypeOf(environment, "android.app.Activity")

    override fun generate(element: Element): FileSpec {
        check(element is TypeElement)
        val parameters = Parameters(environment, element)
        val targetClass = element.asClassName()
        return FileSpec.build(targetClass.packageName, "${targetClass.simpleName}Exercise") {
            if (!element.modifiers.contains(Modifier.ABSTRACT)) {
                addFrom(ParameterizedIntentClassCodeGenerator(element, targetClass, parameters))
            }
            addFrom(GetExtrasClassCodeGenerator(element, targetClass, parameters))

            val resultContractMirror = element.getAnnotation<ResultContract>()
            if (resultContractMirror != null) {
                val kinds = getResultKinds(resultContractMirror)
                addFrom(ResultContractClassCodeGenerator(element, targetClass, kinds))
                addFrom(ResultClassCodeGenerator(element, targetClass, kinds))
                addFrom(ResultSugarFunctionsCodeGenerator(element, targetClass, kinds))
            }
        }
    }
}
