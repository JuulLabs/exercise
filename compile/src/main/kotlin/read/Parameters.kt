package com.juul.exercise.compile.read

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.juul.exercise.annotations.Exercise
import com.juul.exercise.annotations.ExerciseParameter
import com.juul.exercise.annotations.FromStub
import com.juul.exercise.compile.asTypeName
import com.juul.exercise.compile.data.Parameter
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

    val parent = superClass?.findParameters().orEmpty()

    return parent + direct
}

internal fun KSAnnotation.toParameter(): Parameter = Parameter(
    name = getArgument(NAME) as String,
    nonNullTypeName = run {
        val targetClass = getArgument(TYPE) as KSType
        val typeArguments = (getArgument(TYPE_ARGUMENTS) as List<*>).asSequence()
            .filterIsInstance<KSType>()
            .map { it.asTypeName() }
            .toList()
        if (typeArguments.isEmpty()) {
            targetClass.asTypeName()
        } else {
            targetClass.asTypeName().parameterizedBy(typeArguments)
        }
    },
    optional = getArgument(OPTIONAL) as? Boolean ?: OPTIONAL_DEFAULT,
    parceler = (getArgument(PARCELER) as? KSType)
        ?.asTypeName()
        ?.takeUnless { it == Nothing::class.asTypeName() }
)
