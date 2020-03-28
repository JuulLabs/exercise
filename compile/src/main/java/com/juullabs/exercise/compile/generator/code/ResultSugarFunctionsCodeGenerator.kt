package com.juullabs.exercise.compile.generator.code

import com.juullabs.exercise.compile.ResultKind
import com.juullabs.exercise.compile.addFunction
import com.juullabs.exercise.compile.asBundleOf
import com.juullabs.exercise.compile.intentTypeName
import com.juullabs.exercise.compile.reifyResultCode
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec

/*fileSpecBuilder.addFunction("finish") {
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
*/
internal class ResultSugarFunctionsCodeGenerator(
    private val targetClass: ClassName,
    private val resultKinds: List<ResultKind>
) : CodeGenerator {
    override fun addTo(fileSpec: FileSpec.Builder) {
        val className = ClassName(fileSpec.packageName, "${targetClass.simpleName}Result")
        fileSpec.addFunction("finishWith") {
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
