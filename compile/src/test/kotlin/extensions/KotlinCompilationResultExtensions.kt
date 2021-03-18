package com.juul.exercise.compile.extensions

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.kspSourcesDir
import java.io.File

internal fun getGeneratedFile(compilation: KotlinCompilation, name: String): File {
    val generatedFiles = sequence {
        val directories = ArrayDeque<File>().apply { add(compilation.kspSourcesDir) }
        while (directories.isNotEmpty()) {
            val directory = directories.removeLast()
            for (child in directory.listFiles()) {
                if (child.isDirectory) {
                    directories.addLast(child)
                } else {
                    yield(child)
                }
            }
        }
    }.toList()
    return checkNotNull(generatedFiles.firstOrNull { it.name == name }) {
        "Unable to find generated file: $name. Found files: $generatedFiles."
    }
}
