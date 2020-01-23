package com.juullabs.exercise.annotations

import kotlin.reflect.KClass

@Target
@MustBeDocumented
@Retention(AnnotationRetention.SOURCE)
annotation class Extra(val name: String, val type: KClass<*>, val optional: Boolean = false)
