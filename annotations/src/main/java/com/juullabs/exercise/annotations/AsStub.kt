package com.juullabs.exercise.annotations

@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class AsStub(val packageName: String, val className: String)
