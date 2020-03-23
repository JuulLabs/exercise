package com.juullabs.exercise.compile

import javax.annotation.processing.ProcessingEnvironment
import javax.tools.Diagnostic

const val OPTION_PACKAGE_NAME = "exercise.packageName"

fun ProcessingEnvironment.options() = Options(this)

class Options(private val processingEnvironment: ProcessingEnvironment) {
    val packageName: String
        get() = processingEnvironment.options[OPTION_PACKAGE_NAME] ?: run {
            val msg = "Annotation processor argument `exercise.packageName` must be set."
            processingEnvironment.messager.printMessage(Diagnostic.Kind.ERROR, msg)
            error(msg)
        }
}
