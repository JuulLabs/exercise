package com.juul.exercise.compile

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName

internal val TypeName.asNullable
    get() = this.copy(nullable = true)

internal val TypeName.asNonNullable
    get() = this.copy(nullable = false)
