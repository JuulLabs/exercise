package com.juul.exercise.annotations

@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class AsStub(val packageName: String, val className: String)
