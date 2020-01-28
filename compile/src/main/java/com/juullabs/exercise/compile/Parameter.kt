package com.juullabs.exercise.compile

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import com.sun.tools.javac.code.Attribute
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.type.TypeMirror

internal class Parameter(
    val name: String,
    val nonNullTypeName: TypeName,
    val optional: Boolean
) {
    val nullableTypeName = nonNullTypeName.copy(nullable = true)

    /** Either of [nullableTypeName] or [nonNullTypeName] depending on [optional]. */
    val combinedTypeName = if (optional) nullableTypeName else nonNullTypeName

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
            val values = annotation.elementValues.mapKeys { (k, _) -> k.simpleName.toString() }
            val rawNonNullTypeName = adjustTypeName(values["type"]?.value as TypeMirror)
            val arguments = checkNotNull(values["typeArguments"]).value as List<Attribute.Class>
            val nonNullTypeName = if (arguments.isEmpty()) {
                rawNonNullTypeName
            } else {
                val parameters = arguments.map { adjustTypeName(it.classType.asTypeName()) }
                with(ParameterizedTypeName.Companion) {
                    check(rawNonNullTypeName is ClassName)
                    rawNonNullTypeName.parameterizedBy(parameters)
                }
            }
            return Parameter(
                name = values["name"]?.value as String,
                nonNullTypeName = nonNullTypeName,
                optional = values["optional"]?.value == true
            )
        }
    }
}
