package com.juullabs.exercise.annotations

@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Exercise(vararg val extras: ExerciseParameter)
