package com.juullabs.exercise

import com.tschuchort.compiletesting.KotlinCompilation
import java.io.File

fun KotlinCompilation.Result.getGeneratedFile(name: String): File =
    checkNotNull(generatedFiles.firstOrNull { it.name == name }) {
        "Unable to find generated file: $name"
    }
