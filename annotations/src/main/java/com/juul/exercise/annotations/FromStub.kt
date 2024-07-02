package com.juul.exercise.annotations

import kotlin.reflect.KClass

@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class FromStub(
    val source: KClass<*>,
)
