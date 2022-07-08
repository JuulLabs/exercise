package com.juul.exercise.compile.extensions

import org.assertj.core.api.AbstractStringAssert
import org.intellij.lang.annotations.Language

/** Wrapper around [AbstractStringAssert.isEqualTo] for Kotlin code literals to syntax highlight in Android Studio. */
public fun AbstractStringAssert<*>.isEqualToKotlin(
    @Language("kotlin") source: String,
): AbstractStringAssert<*> = this.isEqualTo(source.trimIndent())
