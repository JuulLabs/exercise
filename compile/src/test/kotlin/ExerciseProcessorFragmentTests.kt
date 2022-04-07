package com.juul.exercise.compile

import com.juul.exercise.compile.extensions.getGeneratedFile
import com.juul.exercise.compile.extensions.isEqualToKotlin
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile.Companion.kotlin
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

public class ExerciseProcessorFragmentTests : ExerciseProcessorTests() {

    @Test
    public fun `test fragment generation with no arguments`() {
        val (compilation, result) = compile(
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

        val file = getGeneratedFile(compilation, "NoArgumentsFragmentExercise.kt")
        assertThat(file.readText().trim()).isEqualToKotlin(
            """
            package com.juul.exercise.tests
            
            import android.os.Bundle
            import androidx.core.os.bundleOf
            
            public fun bundleForNoArgumentsFragment(): Bundle = bundleOf()
            
            public fun newNoArgumentsFragment(): NoArgumentsFragment {
              val instance = NoArgumentsFragment()
              instance.arguments = bundleForNoArgumentsFragment()
              return instance
            }
            
            public class NoArgumentsFragmentParams(
              private val instance: NoArgumentsFragment,
            )
            
            public val NoArgumentsFragment.args: NoArgumentsFragmentParams
              get() = NoArgumentsFragmentParams(this)
            """
        )
    }

    @Test
    public fun `test fragment generation with abstract superclass and subclass`() {
        val (compilation, result) = compile(
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

        val superclassFile = getGeneratedFile(compilation, "SuperclassFragmentExercise.kt")
        assertThat(superclassFile.readText().trim()).isEqualToKotlin(
            """
            package com.juul.exercise.tests
            
            import kotlin.Int
            
            public class SuperclassFragmentParams(
              private val instance: SuperclassFragment,
            ) {
              public val fromSuperclass: Int
                get() = instance.arguments?.get("fromSuperclass") as Int
            }
            
            public val SuperclassFragment.args: SuperclassFragmentParams
              get() = SuperclassFragmentParams(this)
            """
        )

        val subclassFile = getGeneratedFile(compilation, "SubclassFragmentExercise.kt")
        assertThat(subclassFile.readText().trim()).isEqualToKotlin(
            """
            package com.juul.exercise.tests
            
            import android.os.Bundle
            import androidx.core.os.bundleOf
            import kotlin.Int
            import kotlin.String
 
            public fun bundleForSubclassFragment(fromSuperclass: Int, fromSubclass: String): Bundle = bundleOf(
              "fromSuperclass" to fromSuperclass,
              "fromSubclass" to fromSubclass
            )
            
            public fun newSubclassFragment(fromSuperclass: Int, fromSubclass: String): SubclassFragment {
              val instance = SubclassFragment()
              instance.arguments = bundleForSubclassFragment(
                fromSuperclass,
                fromSubclass
              )
              return instance
            }
            
            public class SubclassFragmentParams(
              private val instance: SubclassFragment,
            ) {
              public val fromSuperclass: Int
                get() = instance.arguments?.get("fromSuperclass") as Int
            
              public val fromSubclass: String
                get() = instance.arguments?.get("fromSubclass") as String
            }
            
            public val SubclassFragment.args: SubclassFragmentParams
              get() = SubclassFragmentParams(this)
            """
        )
    }

    @Test
    public fun `test fragment generation with generics`() {
        val (compilation, result) = compile(
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

        val file = getGeneratedFile(compilation, "ListFragmentExercise.kt")
        assertThat(file.readText().trim()).isEqualToKotlin(
            """
            package com.juul.exercise.tests
            
            import android.os.Bundle
            import androidx.core.os.bundleOf
            import kotlin.Int
            import kotlin.Suppress
            import kotlin.collections.List
            
            public fun bundleForListFragment(listOfInt: List<Int>): Bundle = bundleOf(
              "listOfInt" to listOfInt
            )
            
            public fun newListFragment(listOfInt: List<Int>): ListFragment {
              val instance = ListFragment()
              instance.arguments = bundleForListFragment(
                listOfInt
              )
              return instance
            }
            
            public class ListFragmentParams(
              private val instance: ListFragment,
            ) {
              public val listOfInt: List<Int>
                @Suppress("UNCHECKED_CAST")
                get() = instance.arguments?.get("listOfInt") as List<Int>
            }
            
            public val ListFragment.args: ListFragmentParams
              get() = ListFragmentParams(this)
            """
        )
    }

    @Test
    public fun `test fragment generation with optionals`() {
        val (compilation, result) = compile(
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

        val file = getGeneratedFile(compilation, "OptionalsFragmentExercise.kt")
        assertThat(file.readText().trim()).isEqualToKotlin(
            """
            package com.juul.exercise.tests
            
            import android.os.Bundle
            import androidx.core.os.bundleOf
            import kotlin.Int
            
            public fun bundleForOptionalsFragment(optionalInt: Int? = null): Bundle = bundleOf(
              "optionalInt" to optionalInt
            )
            
            public fun newOptionalsFragment(optionalInt: Int? = null): OptionalsFragment {
              val instance = OptionalsFragment()
              instance.arguments = bundleForOptionalsFragment(
                optionalInt
              )
              return instance
            }
            
            public class OptionalsFragmentParams(
              private val instance: OptionalsFragment,
            ) {
              public val optionalInt: Int?
                get() = instance.arguments?.get("optionalInt") as Int?
            
              public fun optionalInt(default: Int): Int = (instance.arguments?.get("optionalInt") as? Int?) ?:
                  default
            }
            
            public val OptionalsFragment.args: OptionalsFragmentParams
              get() = OptionalsFragmentParams(this)
            """
        )
    }
}
