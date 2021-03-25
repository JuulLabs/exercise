package com.juul.exercise.compile.data

import com.juul.exercise.compile.asNullable
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName

internal data class Parameter(
    val name: String,
    val nonNullTypeName: TypeName,
    val optional: Boolean,
    val parceler: TypeName?
) {
    val nullableTypeName = nonNullTypeName.asNullable

    /** Either of [nullableTypeName] or [nonNullTypeName] depending on [optional]. */
    val combinedTypeName = if (optional) nullableTypeName else nonNullTypeName

    val isParameterized: Boolean
        get() = nonNullTypeName is ParameterizedTypeName
}
