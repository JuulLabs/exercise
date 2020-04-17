package com.juullabs.exercise

import com.juullabs.exercise.compile.ExerciseProcessor
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.junit.Rule
import org.junit.rules.TemporaryFolder

abstract class ExerciseProcessorTests {

    @Rule
    @JvmField
    val temporaryFolder = TemporaryFolder()

    protected fun compile(vararg files: SourceFile): KotlinCompilation.Result =
        KotlinCompilation().apply {
            workingDir = temporaryFolder.root
            inheritClassPath = true
            sources = files.toList()
            // Dirty hack to get around the fact that I can't seem to get
            // Gradle to see the `Processor` class in Android projects
            this::class.java.declaredMethods
                .single { it.name == "setAnnotationProcessors" }
                .invoke(this, listOf(ExerciseProcessor()))
        }.compile()
}
