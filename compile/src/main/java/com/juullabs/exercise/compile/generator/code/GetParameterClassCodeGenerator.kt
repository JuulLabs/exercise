package com.juullabs.exercise.compile.generator.code

import com.juullabs.exercise.compile.Parameters
import com.juullabs.exercise.compile.addAnnotation
import com.juullabs.exercise.compile.addClass
import com.juullabs.exercise.compile.addFunction
import com.juullabs.exercise.compile.addProperty
import com.juullabs.exercise.compile.asNullable
import com.juullabs.exercise.compile.byteArrayTypeName
import com.juullabs.exercise.compile.createFromMarshalledBytesMemberName
import com.juullabs.exercise.compile.createFromMarshalledBytesOrNullMemberName
import com.juullabs.exercise.compile.getter
import com.juullabs.exercise.compile.primaryConstructor
import com.juullabs.exercise.compile.suppressTypeName
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import javax.lang.model.element.Element

internal abstract class GetParameterClassCodeGenerator(
    private val originatingElement: Element,
    private val targetClass: ClassName,
    private val params: Parameters
) : CodeGenerator {

    abstract val extensionName: String
    abstract val retriever: String

    override fun addTo(fileSpec: FileSpec.Builder) {
        val className = ClassName(fileSpec.packageName, "${targetClass.simpleName}Params")
        fileSpec.addClass(className) {
            originatingElements += originatingElement
            primaryConstructor { addParameter("instance", targetClass) }
            addProperty("instance", targetClass, KModifier.PRIVATE) { initializer("instance") }
            for (param in params.all) {
                val byteArrayTypeName = if (param.optional) byteArrayTypeName.asNullable else byteArrayTypeName
                val createMemberName = if (param.optional) createFromMarshalledBytesOrNullMemberName else createFromMarshalledBytesMemberName
                addProperty(param.name, param.combinedTypeName) {
                    getter {
                        if (param.isParameterized) {
                            addAnnotation(suppressTypeName) { addMember("%S", "UNCHECKED_CAST") }
                        }
                        if (param.parceler == null) {
                            addStatement("return $retriever as %2T", param.name, param.combinedTypeName)
                        } else {
                            addStatement("val data = $retriever as %2T", param.name, byteArrayTypeName)
                            addStatement("return %1T.%2M(data)", param.parceler, createMemberName)
                        }
                    }
                }
                if (param.optional) {
                    addFunction(param.name) {
                        if (param.isParameterized) {
                            addAnnotation(suppressTypeName) { addMember("%S", "UNCHECKED_CAST") }
                        }
                        addParameter("default", param.nonNullTypeName)
                        returns(param.nonNullTypeName)
                        if (param.parceler == null) {
                            addStatement("return ($retriever as? %2T) ?: default", param.name, param.nullableTypeName)
                        } else {
                            addStatement("val data = $retriever as %2T", param.name, byteArrayTypeName)
                            addStatement("return %1T.%2M(data) ?: default", param.parceler, createMemberName)
                        }
                    }
                }
            }
        }
        fileSpec.addProperty(extensionName, className) {
            originatingElements += originatingElement
            receiver(targetClass)
            getter { addStatement("return %T(this)", className) }
        }
    }
}

internal class GetArgumentsClassCodeGenerator(
    originatingElement: Element,
    targetClass: ClassName,
    params: Parameters
) : GetParameterClassCodeGenerator(originatingElement, targetClass, params) {
    override val extensionName = "args"
    override val retriever = "instance.arguments?.get(%1S)"
}

internal class GetExtrasClassCodeGenerator(
    originatingElement: Element,
    targetClass: ClassName,
    params: Parameters
) : GetParameterClassCodeGenerator(originatingElement, targetClass, params) {
    override val extensionName = "extras"
    override val retriever = "instance.intent?.extras?.get(\"\${instance.packageName}.%1L\")"
}
