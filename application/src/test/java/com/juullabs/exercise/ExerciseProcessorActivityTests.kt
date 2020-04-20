package com.juullabs.exercise

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile.Companion.kotlin
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ExerciseProcessorActivityTests : ExerciseProcessorTests() {

    @Test
    fun `test activity generation with no extras`() {
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

        val file = result.getGeneratedFile("NoExtrasActivityExercise.kt")
        assertThat(file.readText().trim()).isEqualToKotlin(
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
            """
        )
    }

    @Test
    fun `test activity generation with abstract superclass and subclass`() {
        val result = compile(
            kotlin(
                "SuperclassActivity.kt",
                """
                package com.juul.exercise.tests
                
                import android.app.Activity
                import com.juullabs.exercise.annotations.Exercise
                import com.juullabs.exercise.annotations.Extra
                
                @Exercise(Extra("fromSuperclass", Int::class))
                abstract class SuperclassActivity : Activity()
                """
            ),
            kotlin(
                "SubclassActivity.kt",
                """
                package com.juul.exercise.tests
                
                import com.juullabs.exercise.annotations.Exercise
                import com.juullabs.exercise.annotations.Extra
                
                @Exercise(Extra("fromSubclass", String::class))
                class SubclassActivity : SuperclassActivity()
                """
            )
        )
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val superclassFile = result.getGeneratedFile("SuperclassActivityExercise.kt")
        assertThat(superclassFile.readText().trim()).isEqualToKotlin(
            """
            package com.juul.exercise.tests
            
            import kotlin.Int
            
            class SuperclassActivityParams(
              private val instance: SuperclassActivity
            ) {
              val fromSuperclass: Int
                get() = instance.intent?.extras?.get("${"$"}{instance.packageName}.fromSuperclass") as Int
            }
            
            val SuperclassActivity.extras: SuperclassActivityParams
              get() = SuperclassActivityParams(this)
            """
        )

        val subclassFile = result.getGeneratedFile("SubclassActivityExercise.kt")
        assertThat(subclassFile.readText().trim()).isEqualToKotlin(
            """
            package com.juul.exercise.tests
            
            import android.content.Context
            import android.content.Intent
            import androidx.core.os.bundleOf
            import kotlin.Int
            import kotlin.String
            
            class SubclassActivityIntent : Intent {
              constructor(
                context: Context,
                fromSuperclass: Int,
                fromSubclass: String
              ) : super() {
                setClassName(context, "com.juul.exercise.tests.SubclassActivity")
                replaceExtras(bundleOf(
                  "${"$"}{context.packageName}.fromSuperclass" to fromSuperclass,
                  "${"$"}{context.packageName}.fromSubclass" to fromSubclass
                ))
              }
            
              constructor(
                packageName: String,
                fromSuperclass: Int,
                fromSubclass: String
              ) : super() {
                setClassName(packageName, "com.juul.exercise.tests.SubclassActivity")
                replaceExtras(bundleOf(
                  "${"$"}{packageName}.fromSuperclass" to fromSuperclass,
                  "${"$"}{packageName}.fromSubclass" to fromSubclass
                ))
              }
            }
            
            class SubclassActivityParams(
              private val instance: SubclassActivity
            ) {
              val fromSuperclass: Int
                get() = instance.intent?.extras?.get("${"$"}{instance.packageName}.fromSuperclass") as Int
            
              val fromSubclass: String
                get() = instance.intent?.extras?.get("${"$"}{instance.packageName}.fromSubclass") as String
            }
            
            val SubclassActivity.extras: SubclassActivityParams
              get() = SubclassActivityParams(this)
            """
        )
    }

    @Test
    fun `test activity generation with generics`() {
        val result = compile(
            kotlin(
                "ListActivity.kt",
                """
                package com.juul.exercise.tests
                
                import android.app.Activity
                import com.juullabs.exercise.annotations.Exercise
                import com.juullabs.exercise.annotations.Extra
                
                @Exercise(Extra("listOfInt", List::class, Int::class))
                class ListActivity : Activity()
                """
            )
        )
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val file = result.getGeneratedFile("ListActivityExercise.kt")
        assertThat(file.readText().trim()).isEqualToKotlin(
            """
            package com.juul.exercise.tests
            
            import android.content.Context
            import android.content.Intent
            import androidx.core.os.bundleOf
            import kotlin.Int
            import kotlin.String
            import kotlin.collections.List
            
            class ListActivityIntent : Intent {
              constructor(context: Context, listOfInt: List<Int>) : super() {
                setClassName(context, "com.juul.exercise.tests.ListActivity")
                replaceExtras(bundleOf(
                  "${"$"}{context.packageName}.listOfInt" to listOfInt
                ))
              }
            
              constructor(packageName: String, listOfInt: List<Int>) : super() {
                setClassName(packageName, "com.juul.exercise.tests.ListActivity")
                replaceExtras(bundleOf(
                  "${"$"}{packageName}.listOfInt" to listOfInt
                ))
              }
            }
            
            class ListActivityParams(
              private val instance: ListActivity
            ) {
              val listOfInt: List<Int>
                get() {
                  @Suppress("UNCHECKED_CAST")
                  return instance.intent?.extras?.get("${"$"}{instance.packageName}.listOfInt") as List<Int>
                }
            }
            
            val ListActivity.extras: ListActivityParams
              get() = ListActivityParams(this)
            """
        )
    }

    @Test
    fun `test activity generation with optionals`() {
        val result = compile(
            kotlin(
                "OptionalsActivity.kt",
                """
                package com.juul.exercise.tests
                
                import android.app.Activity
                import com.juullabs.exercise.annotations.Exercise
                import com.juullabs.exercise.annotations.Extra
                
                @Exercise(Extra("optionalInt", Int::class, optional = true))
                class OptionalsActivity : Activity()
                """
            )
        )
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val file = result.getGeneratedFile("OptionalsActivityExercise.kt")
        assertThat(file.readText().trim()).isEqualToKotlin(
            """
            package com.juul.exercise.tests
            
            import android.content.Context
            import android.content.Intent
            import androidx.core.os.bundleOf
            import kotlin.Int
            import kotlin.String
            
            class OptionalsActivityIntent : Intent {
              constructor(context: Context, optionalInt: Int? = null) : super() {
                setClassName(context, "com.juul.exercise.tests.OptionalsActivity")
                replaceExtras(bundleOf(
                  "${"$"}{context.packageName}.optionalInt" to optionalInt
                ))
              }
            
              constructor(packageName: String, optionalInt: Int? = null) : super() {
                setClassName(packageName, "com.juul.exercise.tests.OptionalsActivity")
                replaceExtras(bundleOf(
                  "${"$"}{packageName}.optionalInt" to optionalInt
                ))
              }
            }
            
            class OptionalsActivityParams(
              private val instance: OptionalsActivity
            ) {
              val optionalInt: Int?
                get() = instance.intent?.extras?.get("${"$"}{instance.packageName}.optionalInt") as Int?
            
              fun optionalInt(default: Int): Int =
                  (instance.intent?.extras?.get("${"$"}{instance.packageName}.optionalInt") as? Int?) ?: default
            }
            
            val OptionalsActivity.extras: OptionalsActivityParams
              get() = OptionalsActivityParams(this)
            """
        )
    }

    @Test
    fun `test activity generation with stubs`() {
        val result = compile(
            kotlin(
                "StubbedActivityStub.kt",
                """
                package com.juul.exercise.tests
                
                import com.juullabs.exercise.annotations.AsStub
                import com.juullabs.exercise.annotations.Exercise
                import com.juullabs.exercise.annotations.Extra
                
                @Exercise(Extra("stubbed", Int::class))
                @AsStub("com.juul.exercise.tests", "StubbedActivity")
                object StubbedActivityStub
                """
            ),
            kotlin(
                "StubbedActivity.kt",
                """
                package com.juul.exercise.tests
                
                import android.app.Activity
                import com.juullabs.exercise.annotations.Exercise
                import com.juullabs.exercise.annotations.FromStub
                
                @Exercise
                @FromStub(StubbedActivityStub::class)
                class StubbedActivity : Activity()
                """
            )
        )
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val stubFile = result.getGeneratedFile("StubbedActivityExerciseStubs.kt")
        assertThat(stubFile.readText().trim()).isEqualToKotlin(
            """
            package com.juul.exercise.tests
            
            import android.content.Context
            import android.content.Intent
            import androidx.core.os.bundleOf
            import kotlin.Int
            import kotlin.String
            
            class StubbedActivityIntent : Intent {
              constructor(context: Context, stubbed: Int) : super() {
                setClassName(context, "com.juul.exercise.tests.StubbedActivity")
                replaceExtras(bundleOf(
                  "${"$"}{context.packageName}.stubbed" to stubbed
                ))
              }
            
              constructor(packageName: String, stubbed: Int) : super() {
                setClassName(packageName, "com.juul.exercise.tests.StubbedActivity")
                replaceExtras(bundleOf(
                  "${"$"}{packageName}.stubbed" to stubbed
                ))
              }
            }
            """
        )

        val paramsFile = result.getGeneratedFile("StubbedActivityExercise.kt")
        assertThat(paramsFile.readText().trim()).isEqualToKotlin(
            """
            package com.juul.exercise.tests
            
            import kotlin.Int
            
            class StubbedActivityParams(
              private val instance: StubbedActivity
            ) {
              val stubbed: Int
                get() = instance.intent?.extras?.get("${"$"}{instance.packageName}.stubbed") as Int
            }
            
            val StubbedActivity.extras: StubbedActivityParams
              get() = StubbedActivityParams(this)
            """
        )
    }
}
