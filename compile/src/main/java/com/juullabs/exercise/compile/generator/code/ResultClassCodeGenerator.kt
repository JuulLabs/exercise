package com.juullabs.exercise.compile.generator.code

import com.juullabs.exercise.compile.ResultKind
import com.juullabs.exercise.compile.addClass
import com.juullabs.exercise.compile.addConstructor
import com.juullabs.exercise.compile.addFunction
import com.juullabs.exercise.compile.addProperty
import com.juullabs.exercise.compile.asBundleOf
import com.juullabs.exercise.compile.asParameterSpecs
import com.juullabs.exercise.compile.bundleTypeName
import com.juullabs.exercise.compile.getter
import com.juullabs.exercise.compile.primaryConstructor
import com.juullabs.exercise.compile.stringTypeName
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec

private const val RETRIEVER = "data.get(\"\$packageName.%1L\")"

internal class ResultClassCodeGenerator(
    private val targetClass: ClassName,
    private val resultKinds: List<ResultKind>
) : CodeGenerator {
    override fun addTo(fileSpec: FileSpec.Builder) {
        val className = ClassName(fileSpec.packageName, "${targetClass.simpleName}Result")
        fileSpec.addClass(className) {
            addModifiers(KModifier.SEALED)
            primaryConstructor { addParameter("data", bundleTypeName) }
            addProperty("data", bundleTypeName, KModifier.INTERNAL) { initializer("data") }

            for (kind in resultKinds) {
                addResultSubclass(className, kind)
            }
        }
    }

    private fun TypeSpec.Builder.addResultSubclass(parent: ClassName, kind: ResultKind) {
        addClass(kind.name) {
            superclass(parent)
            primaryConstructor {
                addParameter("packageName", stringTypeName)
                addParameter("data", bundleTypeName)
            }
            addSuperclassConstructorParameter("data")
            addProperty("packageName", stringTypeName) { initializer("packageName") }
            addConstructor {
                addParameter("packageName", stringTypeName)
                addParameters(kind.params.asParameterSpecs())
                val paramsAsBundle = kind.params.asBundleOf(CodeBlock.of("packageName"))
                callThisConstructor(CodeBlock.of("packageName, %L", paramsAsBundle))
            }
            for (param in kind.params) {
                addProperty(param.name, param.combinedTypeName) {
                    getter {
                        if (param.isParameterized) addStatement("""@Suppress("UNCHECKED_CAST")""")
                        addStatement("return $RETRIEVER as %2T", param.name, param.combinedTypeName)
                    }
                }
                if (param.optional) {
                    addFunction(param.name) {
                        addParameter("default", param.nonNullTypeName)
                        returns(param.nonNullTypeName)
                        if (param.isParameterized) addStatement("""@Suppress("UNCHECKED_CAST")""")
                        addStatement("return ($RETRIEVER as %2T) ?: default", param.name, param.nullableTypeName)
                    }
                }
            }
        }
    }
}
