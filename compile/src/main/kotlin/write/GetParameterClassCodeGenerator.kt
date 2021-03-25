package com.juul.exercise.compile.write

import com.juul.exercise.compile.activityTypeName
import com.juul.exercise.compile.addAnnotation
import com.juul.exercise.compile.addClass
import com.juul.exercise.compile.addFunction
import com.juul.exercise.compile.addProperty
import com.juul.exercise.compile.asNullable
import com.juul.exercise.compile.byteArrayTypeName
import com.juul.exercise.compile.createFromMarshalledBytesMemberName
import com.juul.exercise.compile.createFromMarshalledBytesOrNullMemberName
import com.juul.exercise.compile.data.Parameter
import com.juul.exercise.compile.data.Receiver
import com.juul.exercise.compile.data.Receiver.Fragment
import com.juul.exercise.compile.data.Receiver.Service
import com.juul.exercise.compile.getter
import com.juul.exercise.compile.intentTypeName
import com.juul.exercise.compile.primaryConstructor
import com.juul.exercise.compile.suppressTypeName
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeName

internal abstract class GetParameterClassCodeGenerator<R : Receiver>(
    private val receiver: R,
    private val params: List<Parameter>
) {

    abstract val extensionName: String
    abstract val retriever: String
    abstract val receiverType: TypeName
    open val additionalParams: Map<String, ClassName> = emptyMap()

    fun addParamsTo(fileSpec: FileSpec.Builder) {
        val className = ClassName(fileSpec.packageName, "${receiver.name.simpleName}Params")
        fileSpec.addClass(className) {
            primaryConstructor {
                addParameter("instance", receiverType)
                additionalParams.forEach { (name, type) -> addParameter(name, type) }
            }
            addProperty("instance", receiverType, KModifier.PRIVATE) { initializer("instance") }
            additionalParams.forEach { (name, type) ->
                addProperty(name, type, KModifier.PRIVATE) { initializer(name) }
            }
            for (param in params) {
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
    }

    fun addSugarTo(fileSpec: FileSpec.Builder) {
        val className = ClassName(fileSpec.packageName, "${receiver.name.simpleName}Params")
        if (additionalParams.isEmpty()) {
            fileSpec.addProperty(extensionName, className) {
                receiver(receiver.name)
                getter { addStatement("return %T(this)", className) }
            }
        } else {
            fileSpec.addFunction(extensionName) {
                receiver(receiver.name)
                returns(className)
                additionalParams.forEach { (name, type) -> addParameter(name, type) }
                addStatement("return %T(this, %L)", className, additionalParams.keys.joinToString())
            }
        }
    }
}

internal class GetFragmentArgumentsClassCodeGenerator(
    receiver: Fragment,
    params: List<Parameter>
) : GetParameterClassCodeGenerator<Fragment>(receiver, params) {
    override val extensionName = "args"
    override val retriever = "instance.arguments?.get(%1S)"
    override val receiverType = receiver.name
}

internal class GetActivityExtrasClassCodeGenerator(
    receiver: Receiver,
    params: List<Parameter>
) : GetParameterClassCodeGenerator<Receiver>(receiver, params) {
    override val extensionName = "extras"
    override val retriever = "instance.intent?.extras?.get(\"\${instance.packageName}.%1L\")"
    override val receiverType = activityTypeName
}

internal class GetServiceExtrasClassCodeGenerator(
    receiver: Service,
    params: List<Parameter>
) : GetParameterClassCodeGenerator<Service>(receiver, params) {
    override val extensionName = "extras"
    override val retriever = "intent.extras?.get(\"\${instance.packageName}.%1L\")"
    override val additionalParams = mapOf("intent" to intentTypeName)
    override val receiverType = receiver.name
}
