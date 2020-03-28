@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package com.juullabs.exercise.compile

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import com.sun.tools.javac.code.Attribute
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.type.TypeMirror

internal data class Parameter(
    val name: String,
    val nonNullTypeName: TypeName,
    val optional: Boolean
) {
    val nullableTypeName = nonNullTypeName.asNullable

    /** Either of [nullableTypeName] or [nonNullTypeName] depending on [optional]. */
    val combinedTypeName = if (optional) nullableTypeName else nonNullTypeName

    val isParameterized: Boolean
        get() = nonNullTypeName is ParameterizedTypeName

    companion object {

        private fun adjustTypeName(type: TypeMirror): TypeName =
            adjustTypeName(type.asTypeName())

        private fun adjustTypeName(raw: TypeName): TypeName =
            when ((raw as? ClassName)?.canonicalName) {
                List::class.java.canonicalName -> listTypeName
                String::class.java.canonicalName -> stringTypeName
                else -> raw
            }

        fun fromAnnotation(annotation: AnnotationMirror): Parameter {
            @Suppress("UNCHECKED_CAST")
            val arguments = annotation["typeArguments"] as List<Attribute.Class>
            val rawNonNullTypeName = adjustTypeName(annotation["type"] as TypeMirror)
            val nonNullTypeName = if (arguments.isEmpty()) {
                rawNonNullTypeName
            } else {
                val parameters = arguments.map { adjustTypeName(it.classType.asTypeName()) }
                check(rawNonNullTypeName is ClassName)
                rawNonNullTypeName.parameterizedBy(parameters)
            }
            return Parameter(
                name = annotation["name"] as String,
                nonNullTypeName = nonNullTypeName,
                optional = annotation["optional"] == true
            )
        }
    }
}
