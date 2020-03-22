package com.juullabs.exercise.annotations

@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class ResultContract(vararg val kinds: ResultKind)
