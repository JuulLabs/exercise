package com.juul.exercise.compile.data

import com.squareup.kotlinpoet.ClassName

internal sealed class Receiver {
    abstract val name: ClassName

    data class Activity(
        override val name: ClassName,
        val fromStub: Boolean,
        val isAbstract: Boolean
    ) : Receiver()

    data class Fragment(
        override val name: ClassName,
        val isAbstract: Boolean
    ) : Receiver()

    data class Service(
        override val name: ClassName,
        val isAbstract: Boolean
    ) : Receiver()

    data class Stub(
        override val name: ClassName
    ) : Receiver()
}
