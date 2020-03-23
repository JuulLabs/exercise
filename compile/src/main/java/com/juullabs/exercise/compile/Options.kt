package com.juullabs.exercise.compile

import javax.annotation.processing.ProcessingEnvironment
import javax.tools.Diagnostic

const val OPTION_BUILD_CONFIG_PACKAGE = "exercise.buildConfigPackage"

fun ProcessingEnvironment.options() = Options(this)

class Options(private val processingEnvironment: ProcessingEnvironment) {
    val buildConfigPackage: String
        get() = processingEnvironment.options[OPTION_BUILD_CONFIG_PACKAGE] ?: run {
            val msg = "Annotation processor argument `$OPTION_BUILD_CONFIG_PACKAGE` must be set."
            processingEnvironment.messager.printMessage(Diagnostic.Kind.ERROR, msg)
            error(msg)
        }
}
