package com.juul.exercise.compile.generator.code

import com.juul.exercise.compile.Parameters
import com.juul.exercise.compile.addAnnotation
import com.juul.exercise.compile.addClass
import com.juul.exercise.compile.addFunction
import com.juul.exercise.compile.addProperty
import com.juul.exercise.compile.asNullable
import com.juul.exercise.compile.byteArrayTypeName
import com.juul.exercise.compile.createFromMarshalledBytesMemberName
import com.juul.exercise.compile.createFromMarshalledBytesOrNullMemberName
import com.juul.exercise.compile.getter
import com.juul.exercise.compile.intentTypeName
import com.juul.exercise.compile.primaryConstructor
import com.juul.exercise.compile.suppressTypeName
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
    open val additionalParams: Map<String, ClassName> = emptyMap()

    override fun addTo(fileSpec: FileSpec.Builder) {
        val className = ClassName(fileSpec.packageName, "${targetClass.simpleName}Params")
        fileSpec.addClass(className) {
            originatingElements += originatingElement
            primaryConstructor {
                addParameter("instance", targetClass)
                additionalParams.forEach { (name, type) -> addParameter(name, type) }
            }
            addProperty("instance", targetClass, KModifier.PRIVATE) { initializer("instance") }
            additionalParams.forEach { (name, type) ->
                addProperty(name, type, KModifier.PRIVATE) { initializer(name) }
            }
            for (param in params.all) {
                val byteArrayTypeName = if (param.optional) byteArrayTypeName.asNullable else byteArrayTypeName
                val createMemberName =
                    if (param.optional) createFromMarshalledBytesOrNullMemberName else createFromMarshalledBytesMemberName
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
        if (additionalParams.isEmpty()) {
            fileSpec.addProperty(extensionName, className) {
                originatingElements += originatingElement
                receiver(targetClass)
                getter { addStatement("return %T(this)", className) }
            }
        } else {
            fileSpec.addFunction(extensionName) {
                originatingElements += originatingElement
                receiver(targetClass)
                returns(className)
                additionalParams.forEach { (name, type) -> addParameter(name, type) }
                addStatement("return %T(this, %L)", className, additionalParams.keys.joinToString())
            }
        }
    }
}

internal class GetFragmentArgumentsClassCodeGenerator(
    originatingElement: Element,
    targetClass: ClassName,
    params: Parameters
) : GetParameterClassCodeGenerator(originatingElement, targetClass, params) {
    override val extensionName = "args"
    override val retriever = "instance.arguments?.get(%1S)"
}

internal class GetActivityExtrasClassCodeGenerator(
    originatingElement: Element,
    targetClass: ClassName,
    params: Parameters
) : GetParameterClassCodeGenerator(originatingElement, targetClass, params) {
    override val extensionName = "extras"
    override val retriever = "instance.intent?.extras?.get(\"\${instance.packageName}.%1L\")"
}

internal class GetServiceExtrasClassCodeGenerator(
    originatingElement: Element,
    targetClass: ClassName,
    params: Parameters
) : GetParameterClassCodeGenerator(originatingElement, targetClass, params) {
    override val extensionName = "extras"
    override val retriever = "intent.extras?.get(\"\${instance.packageName}.%1L\")"
    override val additionalParams = mapOf("intent" to intentTypeName)
}
