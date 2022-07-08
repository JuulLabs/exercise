package com.juul.exercise.compile.write

import com.juul.exercise.compile.data.Parameter
import com.juul.exercise.compile.data.Receiver
import com.squareup.kotlinpoet.FileSpec

internal class ExerciseWriter(
    private val receiver: Receiver,
    private val parameters: List<Parameter>,
) {

    fun generateFileSpec(): FileSpec {
        val builder = FileSpec.builder(
            packageName = receiver.name.packageName,
            fileName = when (receiver) {
                is Receiver.Stub -> "${receiver.name.simpleName}ExerciseStubs"
                else -> "${receiver.name.simpleName}Exercise"
            },
        )
        when (receiver) {
            is Receiver.Activity -> generateActivity(builder)
            is Receiver.Fragment -> generateFragment(builder)
            is Receiver.Service -> generateService(builder)
            is Receiver.Stub -> generateStub(builder)
        }
        return builder.build()
    }

    private fun generateActivity(builder: FileSpec.Builder) {
        check(receiver is Receiver.Activity)
        if (!receiver.isAbstract && !receiver.fromStub) {
            ParameterizedIntentClassCodeGenerator(receiver, parameters).addTo(builder)
        }
        val arguments = GetActivityExtrasClassCodeGenerator(receiver, parameters)
        if (!receiver.fromStub) {
            arguments.addParamsTo(builder)
        }
        arguments.addSugarTo(builder)
    }

    private fun generateFragment(builder: FileSpec.Builder) {
        check(receiver is Receiver.Fragment)
        if (!receiver.isAbstract) {
            NewFragmentFunctionCodeGenerator(receiver, parameters).addTo(builder)
        }
        val arguments = GetFragmentArgumentsClassCodeGenerator(receiver, parameters)
        arguments.addParamsTo(builder)
        arguments.addSugarTo(builder)
    }

    private fun generateService(builder: FileSpec.Builder) {
        check(receiver is Receiver.Service)
        if (!receiver.isAbstract) {
            ParameterizedIntentClassCodeGenerator(receiver, parameters).addTo(builder)
        }
        val arguments = GetServiceExtrasClassCodeGenerator(receiver, parameters)
        arguments.addParamsTo(builder)
        arguments.addSugarTo(builder)
    }

    private fun generateStub(builder: FileSpec.Builder) {
        check(receiver is Receiver.Stub)
        ParameterizedIntentClassCodeGenerator(receiver, parameters).addTo(builder)
        val arguments = GetActivityExtrasClassCodeGenerator(receiver, parameters)
        arguments.addParamsTo(builder)
    }
}
