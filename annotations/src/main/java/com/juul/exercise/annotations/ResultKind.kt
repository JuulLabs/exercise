package com.juul.exercise.annotations

@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class ResultKind(
    val name: String,
    vararg val params: ExerciseParameter,
)
