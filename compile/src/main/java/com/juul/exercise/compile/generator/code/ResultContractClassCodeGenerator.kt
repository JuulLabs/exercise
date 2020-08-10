package com.juul.exercise.compile.generator.code

import com.juul.exercise.compile.ResultKind
import com.juul.exercise.compile.activityResultContractTypeName
import com.juul.exercise.compile.addClass
import com.juul.exercise.compile.addFunction
import com.juul.exercise.compile.addProperty
import com.juul.exercise.compile.asNullable
import com.juul.exercise.compile.contextTypeName
import com.juul.exercise.compile.intentTypeName
import com.juul.exercise.compile.primaryConstructor
import com.juul.exercise.compile.reifyResultCode
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import javax.lang.model.element.Element

internal class ResultContractClassCodeGenerator(
    private val originatingElement: Element,
    private val targetClass: ClassName,
    private val resultKinds: List<ResultKind>
): CodeGenerator {
    override fun addTo(fileSpec: FileSpec.Builder) {
        val className = ClassName(fileSpec.packageName, "${targetClass.simpleName}Contract")
        val intentName = ClassName(fileSpec.packageName, "${targetClass.simpleName}Intent")
        val resultName = ClassName(fileSpec.packageName, "${targetClass.simpleName}Result")
        fileSpec.addClass(className) {
            originatingElements += originatingElement
            superclass(activityResultContractTypeName.parameterizedBy(intentName, resultName.asNullable))

            primaryConstructor { addParameter("context", contextTypeName) }
            addProperty("context", contextTypeName, KModifier.PRIVATE) { initializer("context") }

            addFunction("createIntent") {
                addModifiers(KModifier.OVERRIDE)
                addParameter("input", intentName)
                returns(intentTypeName)
                addStatement("return input")
            }

            addFunction("parseResult") {
                addModifiers(KModifier.OVERRIDE)
                addParameter("resultCode", Int::class)
                addParameter("intent", intentTypeName.asNullable)
                returns(resultName.asNullable)
                addStatement("val data = intent?.extras ?: return null")
                beginControlFlow("return when (resultCode) {")
                for (kind in resultKinds) {
                    addStatement(
                        "%L -> %T(context.packageName, data)",
                        reifyResultCode(kind.code),
                        resultName.nestedClass(kind.name)
                    )
                }
                addStatement(
                    "else -> throw IllegalStateException(%S)",
                    "Unknown result code for ${resultName.simpleName}."
                )
                endControlFlow()
            }
        }
    }
}
