package com.juullabs.exercise

import com.juullabs.exercise.compile.ExerciseProcessor
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.SourceFile.Companion.kotlin
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class ExerciseProcessorTests {

    @Rule
    @JvmField
    val temporaryFolder = TemporaryFolder()

    private fun compile(vararg files: SourceFile): KotlinCompilation.Result =
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

    @Test
    fun `test no extras activity generation`() {
        val result = compile(
            kotlin(
                "NoExtrasActivity.kt",
                """
                package com.juul.exercise.tests
                
                import android.app.Activity
                import com.juullabs.exercise.annotations.Exercise
               
                @Exercise
                class NoExtrasActivity : Activity()
                """
            )
        )
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val file = result.generatedFiles.single { it.extension == "kt" }
        assertThat(file.name).isEqualTo("NoExtrasActivityExercise.kt")
        assertThat(file.readText().trim()).isEqualTo(
            """
            package com.juul.exercise.tests
            
            import android.content.Context
            import android.content.Intent
            import kotlin.String
            
            class NoExtrasActivityIntent : Intent {
              constructor(context: Context) : super() {
                setClassName(context, "com.juul.exercise.tests.NoExtrasActivity")
              }
            
              constructor(packageName: String) : super() {
                setClassName(packageName, "com.juul.exercise.tests.NoExtrasActivity")
              }
            }
            
            class NoExtrasActivityParams(
              private val instance: NoExtrasActivity
            )
            
            val NoExtrasActivity.extras: NoExtrasActivityParams
              get() = NoExtrasActivityParams(this)
            """.trimIndent()
        )
    }
}
