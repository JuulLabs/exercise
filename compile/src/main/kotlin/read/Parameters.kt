package com.juul.exercise.compile.read

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.juul.exercise.annotations.Exercise
import com.juul.exercise.annotations.ExerciseParameter
import com.juul.exercise.annotations.FromStub
import com.juul.exercise.compile.asTypeName
import com.juul.exercise.compile.data.Parameter
import com.juul.exercise.compile.loggable
import com.juul.tuulbox.logging.Log
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.asTypeName

private val PARAMS = Exercise::params.name

private val NAME = ExerciseParameter::name.name
private val TYPE = ExerciseParameter::type.name
private val TYPE_ARGUMENTS = ExerciseParameter::typeArguments.name
private val OPTIONAL = ExerciseParameter::optional.name
private val PARCELER = ExerciseParameter::parceler.name

private val SOURCE = FromStub::source.name

// Would be nice to actually tie this to the [ExerciseParameter] class somehow.
private const val OPTIONAL_DEFAULT = false

internal fun KSClassDeclaration.findParameters(): List<Parameter> {
    val direct = getAnnotation<Exercise>()
        ?.getArgument(PARAMS)
        ?.let { it as List<*> }
        ?.mapNotNull { (it as? KSAnnotation)?.toParameter() }
        .orEmpty()

    val stub = getAnnotation<FromStub>()
        ?.getArgument(SOURCE)
        ?.let { it as KSType }
        ?.declaration
        ?.let { it as KSClassDeclaration }
        ?.findParameters()
        .orEmpty()

    val parent = superClass?.findParameters().orEmpty()

    return parent + stub + direct
}

internal fun KSAnnotation.toParameter(): Parameter {
    val parameter = Parameter(
        name = this.getArgument(NAME) as String,
        nonNullTypeName = run {
            val targetClass = this.getArgument(TYPE) as KSType
            val typeArguments = (this.getArgument(TYPE_ARGUMENTS) as List<*>).asSequence()
                .filterIsInstance<KSType>()
                .map { it.asTypeName() }
                .toList()
            if (typeArguments.isEmpty()) {
                targetClass.asTypeName()
            } else {
                targetClass.asTypeName().parameterizedBy(typeArguments)
            }
        },
        optional = this.getArgument(OPTIONAL) as? Boolean ?: OPTIONAL_DEFAULT,
        parceler = (this.getArgument(PARCELER) as? KSType)
            ?.asTypeName()
            ?.takeUnless { it == Nothing::class.asTypeName() }
    )
    return parameter
}
