package com.juul.exercise

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile.Companion.kotlin
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ExerciseProcessorFragmentTests : ExerciseProcessorTests() {

    @Test
    fun `test fragment generation with no arguments`() {
        val result = compile(
            kotlin(
                "NoArgumentsFragment.kt",
                """
                package com.juul.exercise.tests
                
                import androidx.fragment.app.Fragment
                import com.juul.exercise.annotations.Exercise
                
                @Exercise
                class NoArgumentsFragment : Fragment()
                """
            )
        )
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val file = result.getGeneratedFile("NoArgumentsFragmentExercise.kt")
        assertThat(file.readText().trim()).isEqualToKotlin(
            """
            package com.juul.exercise.tests
            
            import androidx.core.os.bundleOf
            
            fun newNoArgumentsFragment(): NoArgumentsFragment {
              val instance = NoArgumentsFragment()
              return instance.apply {
                arguments = bundleOf()
              }
            }
            
            class NoArgumentsFragmentParams(
              private val instance: NoArgumentsFragment
            )
            
            val NoArgumentsFragment.args: NoArgumentsFragmentParams
              get() = NoArgumentsFragmentParams(this)
            """
        )
    }

    @Test
    fun `test fragment generation with abstract superclass and subclass`() {
        val result = compile(
            kotlin(
                "SuperclassFragment.kt",
                """
                package com.juul.exercise.tests
                
                import androidx.fragment.app.Fragment
                import com.juul.exercise.annotations.Exercise
                import com.juul.exercise.annotations.Extra
                
                @Exercise(Extra("fromSuperclass", Int::class))
                abstract class SuperclassFragment : Fragment()
                """
            ),
            kotlin(
                "SubclassFragment.kt",
                """
                package com.juul.exercise.tests
                
                import com.juul.exercise.annotations.Exercise
                import com.juul.exercise.annotations.Extra
                
                @Exercise(Extra("fromSubclass", String::class))
                class SubclassFragment : SuperclassFragment()
                """
            )
        )
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val superclassFile = result.getGeneratedFile("SuperclassFragmentExercise.kt")
        assertThat(superclassFile.readText().trim()).isEqualToKotlin(
            """
            package com.juul.exercise.tests
            
            import kotlin.Int
            
            class SuperclassFragmentParams(
              private val instance: SuperclassFragment
            ) {
              val fromSuperclass: Int
                get() = instance.arguments?.get("fromSuperclass") as Int
            }
            
            val SuperclassFragment.args: SuperclassFragmentParams
              get() = SuperclassFragmentParams(this)
            """
        )

        val subclassFile = result.getGeneratedFile("SubclassFragmentExercise.kt")
        assertThat(subclassFile.readText().trim()).isEqualToKotlin(
            """
            package com.juul.exercise.tests
            
            import androidx.core.os.bundleOf
            import kotlin.Int
            import kotlin.String
            
            fun newSubclassFragment(fromSuperclass: Int, fromSubclass: String): SubclassFragment {
              val instance = SubclassFragment()
              return instance.apply {
                arguments = bundleOf(
                  "fromSuperclass" to fromSuperclass,
                  "fromSubclass" to fromSubclass
                )
              }
            }
            
            class SubclassFragmentParams(
              private val instance: SubclassFragment
            ) {
              val fromSuperclass: Int
                get() = instance.arguments?.get("fromSuperclass") as Int
            
              val fromSubclass: String
                get() = instance.arguments?.get("fromSubclass") as String
            }
            
            val SubclassFragment.args: SubclassFragmentParams
              get() = SubclassFragmentParams(this)
            """
        )
    }

    @Test
    fun `test fragment generation with generics`() {
        val result = compile(
            kotlin(
                "ListFragment.kt",
                """
                package com.juul.exercise.tests
                
                import androidx.fragment.app.Fragment
                import com.juul.exercise.annotations.Exercise
                import com.juul.exercise.annotations.Extra
                
                @Exercise(Extra("listOfInt", List::class, Int::class))
                class ListFragment : Fragment()
                """
            )
        )
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val file = result.getGeneratedFile("ListFragmentExercise.kt")
        assertThat(file.readText().trim()).isEqualToKotlin(
            """
            package com.juul.exercise.tests
            
            import androidx.core.os.bundleOf
            import kotlin.Int
            import kotlin.Suppress
            import kotlin.collections.List
            
            fun newListFragment(listOfInt: List<Int>): ListFragment {
              val instance = ListFragment()
              return instance.apply {
                arguments = bundleOf(
                  "listOfInt" to listOfInt
                )
              }
            }
            
            class ListFragmentParams(
              private val instance: ListFragment
            ) {
              val listOfInt: List<Int>
                @Suppress("UNCHECKED_CAST")
                get() = instance.arguments?.get("listOfInt") as List<Int>
            }
            
            val ListFragment.args: ListFragmentParams
              get() = ListFragmentParams(this)
            """
        )
    }

    @Test
    fun `test fragment generation with optionals`() {
        val result = compile(
            kotlin(
                "OptionalsFragment.kt",
                """
                package com.juul.exercise.tests
                
                import androidx.fragment.app.Fragment
                import com.juul.exercise.annotations.Exercise
                import com.juul.exercise.annotations.Extra
                
                @Exercise(Extra("optionalInt", Int::class, optional = true))
                class OptionalsFragment : Fragment()
                """
            )
        )
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val file = result.getGeneratedFile("OptionalsFragmentExercise.kt")
        assertThat(file.readText().trim()).isEqualToKotlin(
            """
            package com.juul.exercise.tests
            
            import androidx.core.os.bundleOf
            import kotlin.Int
            
            fun newOptionalsFragment(optionalInt: Int? = null): OptionalsFragment {
              val instance = OptionalsFragment()
              return instance.apply {
                arguments = bundleOf(
                  "optionalInt" to optionalInt
                )
              }
            }
            
            class OptionalsFragmentParams(
              private val instance: OptionalsFragment
            ) {
              val optionalInt: Int?
                get() = instance.arguments?.get("optionalInt") as Int?
            
              fun optionalInt(default: Int): Int = (instance.arguments?.get("optionalInt") as? Int?) ?: default
            }
            
            val OptionalsFragment.args: OptionalsFragmentParams
              get() = OptionalsFragmentParams(this)
            """
        )
    }
}
