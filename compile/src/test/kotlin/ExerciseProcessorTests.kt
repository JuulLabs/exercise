package com.juul.exercise.compile

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.jetbrains.kotlin.config.JvmTarget
import org.junit.Rule
import org.junit.rules.TemporaryFolder

public abstract class ExerciseProcessorTests {

    @Rule
    @JvmField
    public val temporaryFolder: TemporaryFolder = TemporaryFolder()

    protected fun compile(vararg files: SourceFile): Pair<KotlinCompilation, KotlinCompilation.Result> {
        val compilation = KotlinCompilation().apply {
            workingDir = temporaryFolder.root
            jvmTarget = JvmTarget.JVM_1_8.description
            inheritClassPath = true
            sources = files.toList()
            symbolProcessorProviders = listOf(ExerciseProcessorProvider())
        }
        return compilation to compilation.compile()
    }
}
