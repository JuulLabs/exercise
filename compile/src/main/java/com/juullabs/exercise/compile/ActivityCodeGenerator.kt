package com.juullabs.exercise.compile

import com.juullabs.exercise.annotations.ResultContract
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

internal class ActivityCodeGenerator(
    environment: ProcessingEnvironment,
    type: TypeElement
) : CodeGenerator(environment, type) {

    override val exerciseClassName: String = "${typeName}Extras"
    override val exerciseSugarName: String = "extras"
    override val codeToRetrieveFromInstance: String = "%1N.intent?.extras?.get(\"\${%1N.packageName}.%2L\")"
    private val codeToRetrieveFromResult: String = "%1N.get(\"\$packageName.%2L\")"
    private val generatedIntentTypeName = ClassName(typePackage, "${typeName}Intent")
    private val resultTypeName = ClassName(typePackage, "${typeName}Result")

    override fun onBuild(fileSpecBuilder: FileSpec.Builder) {
        if (!typeIsAbstract) {
            fileSpecBuilder.addImport("android.app", "Activity")
            fileSpecBuilder.addImport("androidx.core.os", "bundleOf")
            fileSpecBuilder.addType(generateIntentClass())
        }
        val resultContractMirror = type.getAnnotationMirror<ResultContract>()
        if (resultContractMirror != null) {
            if (typeIsAbstract) {
                val msg = "Cannot generate result contract for abstract class."
                environment.messager.printMessage(Diagnostic.Kind.ERROR, msg)
                error(msg)
            }
            val kinds = getResultKindsForMirror(resultContractMirror)
            generateResultClass(fileSpecBuilder, kinds)
            generateFinishSugar(fileSpecBuilder, kinds)
            generateResultContract(fileSpecBuilder, kinds)
        }
    }

    private fun generateIntentClass(): TypeSpec {
        val extras = parameters.all
        return TypeSpec.buildClass(generatedIntentTypeName.simpleName) {
            superclass(intentTypeName)
            fun addConstructor(
                packageNameElement: String,
                packageNameElementType: TypeName,
                prefix: String
            ) {
                addConstructor {
                    callSuperConstructor()
                    addParameter(packageNameElement, packageNameElementType)
                    addParameters(extras.asParameterSpecs())
                    addStatement("setClassName(%L, %S)", packageNameElement, typeClassName)
                    if (extras.isNotEmpty()) {
                        addCode("replaceExtras(%L)\n", extras.toBundle(prefix))
                    }
                }
            }
            addConstructor("context", contextTypeName, "context.packageName")
            addConstructor("packageName", stringTypeName, "packageName")
        }
    }

    private fun generateResultClass(fileSpecBuilder: FileSpec.Builder, resultKinds: List<ResultKind>) {
        fileSpecBuilder.addClass(resultTypeName.simpleName) {
            addModifiers(KModifier.SEALED)
            primaryConstructor { addParameter("data", bundleTypeName) }
            addProperty("data", bundleTypeName) { initializer("data") }

            for (kind in resultKinds) {
                addClass(kind.name) {
                    superclass(resultTypeName)
                    primaryConstructor {
                        addModifiers(KModifier.INTERNAL)
                        addParameter("packageName", stringTypeName)
                        addParameter("data", bundleTypeName)
                    }
                    addSuperclassConstructorParameter("data")
                    addProperty("packageName", stringTypeName) { initializer("packageName") }
                    addConstructor {
                        addParameter("packageName", stringTypeName)
                        addParameters(kind.params.asParameterSpecs())
                        callThisConstructor("packageName", kind.params.toBundle("packageName").toString())
                    }
                    for (param in kind.params) {
                        addExerciseParameter(param, codeToRetrieveFromResult, "data")
                    }
                }
            }
        }
    }

    private fun generateFinishSugar(fileSpecBuilder: FileSpec.Builder, resultKinds: List<ResultKind>) {
        fileSpecBuilder.addFunction("finish") {
            receiver(typeClassName)
            addParameter("result", resultTypeName)
            beginControlFlow("val code = when (result) {")
            for (kind in resultKinds) {
                addStatement(
                    "is %T -> %L",
                    resultTypeName.nestedClass(kind.name),
                    reifyResultCode(kind.code)
                )
            }
            endControlFlow()
            addStatement("val data = Intent().apply { replaceExtras(result.data) }")
            addStatement("setResult(code, data)")
            addStatement("finish()")
        }

        for (kind in resultKinds) {
            fileSpecBuilder.addFunction("finishWith${kind.name}") {
                receiver(typeClassName)
                for (param in kind.params) {
                    addParameter(param.name, param.combinedTypeName)
                }
                addCode(
                    "finish(%T(packageName, %L))",
                    resultTypeName.nestedClass(kind.name),
                    kind.params.toBundle("packageName")
                )
            }
        }
    }

    private fun generateResultContract(fileSpecBuilder: FileSpec.Builder, resultKinds: List<ResultKind>) {
        val nullableResultType = resultTypeName.copy(nullable = true)
        fileSpecBuilder.addClass("${typeName}Contract") {
            superclass(with(ParameterizedTypeName.Companion) {
                activityResultContractTypeName.parameterizedBy(generatedIntentTypeName, nullableResultType)
            })

            primaryConstructor { addParameter("context", contextTypeName) }
            addProperty("context", contextTypeName, KModifier.PRIVATE) { initializer("context") }

            addFunction("createIntent") {
                addModifiers(KModifier.OVERRIDE)
                addParameter("input", generatedIntentTypeName)
                returns(intentTypeName)
                addStatement("return input")
            }

            addFunction("parseResult") {
                addModifiers(KModifier.OVERRIDE)
                addParameter("resultCode", Int::class)
                addParameter("intent", intentTypeName.copy(nullable = true))
                returns(nullableResultType)
                addStatement("val data = intent?.extras ?: return null")
                beginControlFlow("return when (resultCode) {")
                for (kind in resultKinds) {
                    addStatement(
                        "%L -> %T(context.packageName, data)",
                        reifyResultCode(kind.code),
                        resultTypeName.nestedClass(kind.name)
                    )
                }
                addStatement(
                    "else -> throw IllegalStateException(%S)",
                    "Unknown result code for ${resultTypeName.simpleName}."
                )
                endControlFlow()
            }
        }
    }

    private fun reifyResultCode(code: Int): String = when (code) {
        -1 -> "Activity.RESULT_OK"
        0 -> "Activity.RESULT_CANCELED"
        1 -> "Activity.RESULT_FIRST_USER"
        else -> "Activity.RESULT_FIRST_USER + ${code - 1}"
    }
}
