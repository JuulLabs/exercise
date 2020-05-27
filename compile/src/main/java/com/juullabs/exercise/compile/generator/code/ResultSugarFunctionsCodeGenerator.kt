package com.juullabs.exercise.compile.generator.code

import com.juullabs.exercise.compile.*
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import javax.lang.model.element.Element

internal class ResultSugarFunctionsCodeGenerator(
    private val originatingElement: Element,
    private val targetClass: ClassName,
    private val resultKinds: List<ResultKind>
) : CodeGenerator {
    override fun addTo(fileSpec: FileSpec.Builder) {
        val className = ClassName(fileSpec.packageName, "${targetClass.simpleName}Result")
        fileSpec.addFunction("finishWith") {
            originatingElements += originatingElement
            receiver(targetClass)
            addParameter("result", className)
            beginControlFlow("val code = when (result) {")
            for (kind in resultKinds) {
                addStatement("is %T -> %L", className.nestedClass(kind.name), reifyResultCode(kind.code))
            }
            endControlFlow()
            addStatement("setResult(code, %T().apply { replaceExtras(result.data) })", intentTypeName)
            addStatement("finish()")
        }

        for (kind in resultKinds) {
            fileSpec.addFunction("finishWith${kind.name}") {
                receiver(targetClass)
                for (param in kind.params) {
                    addParameter(param.name, param.combinedTypeName)
                }
                addCode(
                    "finishWith(%T(packageName, %L))",
                    className.nestedClass(kind.name),
                    kind.params.asBundleOf(CodeBlock.of("packageName"))
                )
            }
        }
    }
}
