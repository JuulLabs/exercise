package com.juullabs.exercise.compile

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
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

        private fun adjustTypeName(type: TypeMirror): TypeName {
            val rawTypeName = type.asTypeName()
            return when ((rawTypeName as? ClassName)?.canonicalName) {
                String::class.java.canonicalName -> stringTypeName
                else -> rawTypeName
            }
        }

        fun fromAnnotation(annotation: AnnotationMirror): Parameter {
            val values = annotation.elementValues.mapKeys { (k, _) -> k.simpleName.toString() }
            return Parameter(
                name = checkNotNull(values["name"]).value as String,
                nonNullTypeName = adjustTypeName(checkNotNull(values["type"]).value as TypeMirror),
                optional = values["optional"]?.value == true
            )
        }
    }
}
