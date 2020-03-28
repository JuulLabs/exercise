package com.juullabs.exercise.annotations

import kotlin.reflect.KClass

@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class FromStub(val source: KClass<*>)

