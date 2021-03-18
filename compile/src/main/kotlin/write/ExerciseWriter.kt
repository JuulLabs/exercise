package com.juul.exercise.compile.write

import com.juul.exercise.compile.data.Parameter
import com.juul.exercise.compile.data.Receiver
import com.squareup.kotlinpoet.FileSpec

internal class ExerciseWriter(
    private val receiver: Receiver,
    private val parameters: List<Parameter>
) {

    fun generateFileSpec(): FileSpec {
        val builder = FileSpec.builder(
            packageName = receiver.name.packageName,
            fileName = when (receiver) {
                is Receiver.Stub ->  "${receiver.name.simpleName}ExerciseStubs"
                else -> "${receiver.name.simpleName}Exercise"
            }
        )
        return builder.build()
    }
}
