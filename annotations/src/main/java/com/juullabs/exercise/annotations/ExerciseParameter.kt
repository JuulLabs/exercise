package com.juullabs.exercise.annotations

import kotlin.reflect.KClass

/** Actual type used by [Exercise]. Usually, use [Extra] or [Argument] aliases instead. */
@Target
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
annotation class ExerciseParameter(
    val name: String,
    val type: KClass<*>,
    vararg val typeArguments: KClass<*>,
    val optional: Boolean = false
)

/** Type used by [Exercise] on an `Activity`. */
typealias Extra = ExerciseParameter

/** Type used by [Exercise] on a `Fragment`. */
typealias Argument = ExerciseParameter
