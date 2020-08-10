package com.juul.exercise.annotations

@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class Exercise(vararg val params: ExerciseParameter)
