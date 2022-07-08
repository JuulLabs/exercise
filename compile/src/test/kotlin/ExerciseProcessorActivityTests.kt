package com.juul.exercise.compile

import com.juul.exercise.compile.extensions.getGeneratedFile
import com.juul.exercise.compile.extensions.isEqualToKotlin
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile.Companion.kotlin
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

public class ExerciseProcessorActivityTests : ExerciseProcessorTests() {

    @Test
    public fun `test activity generation with no extras`() {
        val (compilation, result) = compile(
            kotlin(
                "NoExtrasActivity.kt",
                """
                package com.juul.exercise.tests
                
                import android.app.Activity
                import com.juul.exercise.annotations.Exercise
                
                @Exercise
                class NoExtrasActivity : Activity()
                """,
            ),
        )
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val file = getGeneratedFile(compilation, "NoExtrasActivityExercise.kt")
        assertThat(file.readText().trim()).isEqualToKotlin(
            """
            package com.juul.exercise.tests
            
            import android.app.Activity
            import android.content.Context
            import android.content.Intent
            import android.os.Bundle
            import androidx.core.os.bundleOf
            import kotlin.String

            public fun bundleForNoExtrasActivity(context: Context): Bundle = bundleOf()

            public fun bundleForNoExtrasActivity(packageName: String): Bundle = bundleOf()

            public class NoExtrasActivityIntent : Intent {
              public constructor(context: Context) : super() {
                setClassName(context, "com.juul.exercise.tests.NoExtrasActivity")
              }
            
              public constructor(packageName: String) : super() {
                setClassName(packageName, "com.juul.exercise.tests.NoExtrasActivity")
              }
            }
            
            public class NoExtrasActivityParams(
              private val instance: Activity,
            )
            
            public val NoExtrasActivity.extras: NoExtrasActivityParams
              get() = NoExtrasActivityParams(this)
            """,
        )
    }

    @Test
    public fun `test activity generation with abstract superclass and subclass`() {
        val (compilation, result) = compile(
            kotlin(
                "SuperclassActivity.kt",
                """
                package com.juul.exercise.tests
                
                import android.app.Activity
                import com.juul.exercise.annotations.Exercise
                import com.juul.exercise.annotations.Extra
                
                @Exercise(Extra("fromSuperclass", Int::class))
                abstract class SuperclassActivity : Activity()
                """,
            ),
            kotlin(
                "SubclassActivity.kt",
                """
                package com.juul.exercise.tests
                
                import com.juul.exercise.annotations.Exercise
                import com.juul.exercise.annotations.Extra
                
                @Exercise(Extra("fromSubclass", String::class))
                class SubclassActivity : SuperclassActivity()
                """,
            ),
        )
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val superclassFile = getGeneratedFile(compilation, "SuperclassActivityExercise.kt")
        assertThat(superclassFile.readText().trim()).isEqualToKotlin(
            """
            package com.juul.exercise.tests
            
            import android.app.Activity
            import kotlin.Int
            
            public class SuperclassActivityParams(
              private val instance: Activity,
            ) {
              public val fromSuperclass: Int
                get() = instance.intent?.extras?.get("${"$"}{instance.packageName}.fromSuperclass") as Int
            }
            
            public val SuperclassActivity.extras: SuperclassActivityParams
              get() = SuperclassActivityParams(this)
            """,
        )

        val subclassFile = getGeneratedFile(compilation, "SubclassActivityExercise.kt")
        assertThat(subclassFile.readText().trim()).isEqualToKotlin(
            """
            package com.juul.exercise.tests
            
            import android.app.Activity
            import android.content.Context
            import android.content.Intent
            import android.os.Bundle
            import androidx.core.os.bundleOf
            import kotlin.Int
            import kotlin.String
            
            public fun bundleForSubclassActivity(
              context: Context,
              fromSuperclass: Int,
              fromSubclass: String,
            ): Bundle = bundleOf(
              "${"$"}{context.packageName}.fromSuperclass" to fromSuperclass,
              "${"$"}{context.packageName}.fromSubclass" to fromSubclass
            )

            public fun bundleForSubclassActivity(
              packageName: String,
              fromSuperclass: Int,
              fromSubclass: String,
            ): Bundle = bundleOf(
              "${"$"}{packageName}.fromSuperclass" to fromSuperclass,
              "${"$"}{packageName}.fromSubclass" to fromSubclass
            )

            public class SubclassActivityIntent : Intent {
              public constructor(
                context: Context,
                fromSuperclass: Int,
                fromSubclass: String,
              ) : super() {
                setClassName(context, "com.juul.exercise.tests.SubclassActivity")
                replaceExtras(bundleForSubclassActivity(
                  context.packageName,
                  fromSuperclass,
                  fromSubclass
                ))
              }
            
              public constructor(
                packageName: String,
                fromSuperclass: Int,
                fromSubclass: String,
              ) : super() {
                setClassName(packageName, "com.juul.exercise.tests.SubclassActivity")
                replaceExtras(bundleForSubclassActivity(
                  packageName,
                  fromSuperclass,
                  fromSubclass
                ))
              }
            }
            
            public class SubclassActivityParams(
              private val instance: Activity,
            ) {
              public val fromSuperclass: Int
                get() = instance.intent?.extras?.get("${"$"}{instance.packageName}.fromSuperclass") as Int
            
              public val fromSubclass: String
                get() = instance.intent?.extras?.get("${"$"}{instance.packageName}.fromSubclass") as String
            }
            
            public val SubclassActivity.extras: SubclassActivityParams
              get() = SubclassActivityParams(this)
            """,
        )
    }

    @Test
    public fun `test activity generation with generics`() {
        val (compilation, result) = compile(
            kotlin(
                "ListActivity.kt",
                """
                package com.juul.exercise.tests
                
                import android.app.Activity
                import com.juul.exercise.annotations.Exercise
                import com.juul.exercise.annotations.Extra
                
                @Exercise(Extra("listOfInt", List::class, Int::class))
                class ListActivity : Activity()
                """,
            ),
        )
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val file = getGeneratedFile(compilation, "ListActivityExercise.kt")
        assertThat(file.readText().trim()).isEqualToKotlin(
            """
            package com.juul.exercise.tests
            
            import android.app.Activity
            import android.content.Context
            import android.content.Intent
            import android.os.Bundle
            import androidx.core.os.bundleOf
            import kotlin.Int
            import kotlin.String
            import kotlin.Suppress
            import kotlin.collections.List
            
            public fun bundleForListActivity(context: Context, listOfInt: List<Int>): Bundle = bundleOf(
              "${"$"}{context.packageName}.listOfInt" to listOfInt
            )

            public fun bundleForListActivity(packageName: String, listOfInt: List<Int>): Bundle = bundleOf(
              "${"$"}{packageName}.listOfInt" to listOfInt
            )
 
            public class ListActivityIntent : Intent {
              public constructor(context: Context, listOfInt: List<Int>) : super() {
                setClassName(context, "com.juul.exercise.tests.ListActivity")
                replaceExtras(bundleForListActivity(
                  context.packageName,
                  listOfInt
                ))
              }
            
              public constructor(packageName: String, listOfInt: List<Int>) : super() {
                setClassName(packageName, "com.juul.exercise.tests.ListActivity")
                replaceExtras(bundleForListActivity(
                  packageName,
                  listOfInt
                ))
              }
            }
            
            public class ListActivityParams(
              private val instance: Activity,
            ) {
              public val listOfInt: List<Int>
                @Suppress("UNCHECKED_CAST")
                get() = instance.intent?.extras?.get("${"$"}{instance.packageName}.listOfInt") as List<Int>
            }
            
            public val ListActivity.extras: ListActivityParams
              get() = ListActivityParams(this)
            """,
        )
    }

    @Test
    public fun `test activity generation with optionals`() {
        val (compilation, result) = compile(
            kotlin(
                "OptionalsActivity.kt",
                """
                package com.juul.exercise.tests
                
                import android.app.Activity
                import com.juul.exercise.annotations.Exercise
                import com.juul.exercise.annotations.Extra
                
                @Exercise(Extra("optionalInt", Int::class, optional = true))
                class OptionalsActivity : Activity()
                """,
            ),
        )
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val file = getGeneratedFile(compilation, "OptionalsActivityExercise.kt")
        assertThat(file.readText().trim()).isEqualToKotlin(
            """
            package com.juul.exercise.tests
            
            import android.app.Activity
            import android.content.Context
            import android.content.Intent
            import android.os.Bundle
            import androidx.core.os.bundleOf
            import kotlin.Int
            import kotlin.String
            
            public fun bundleForOptionalsActivity(context: Context, optionalInt: Int? = null): Bundle =
                bundleOf(
              "${"$"}{context.packageName}.optionalInt" to optionalInt
            )
            
            public fun bundleForOptionalsActivity(packageName: String, optionalInt: Int? = null): Bundle =
                bundleOf(
              "${"${'$'}"}{packageName}.optionalInt" to optionalInt
            )
            
            public class OptionalsActivityIntent : Intent {
              public constructor(context: Context, optionalInt: Int? = null) : super() {
                setClassName(context, "com.juul.exercise.tests.OptionalsActivity")
                replaceExtras(bundleForOptionalsActivity(
                  context.packageName,
                  optionalInt
                ))
              }
            
              public constructor(packageName: String, optionalInt: Int? = null) : super() {
                setClassName(packageName, "com.juul.exercise.tests.OptionalsActivity")
                replaceExtras(bundleForOptionalsActivity(
                  packageName,
                  optionalInt
                ))
              }
            }
            
            public class OptionalsActivityParams(
              private val instance: Activity,
            ) {
              public val optionalInt: Int?
                get() = instance.intent?.extras?.get("${"$"}{instance.packageName}.optionalInt") as Int?
            
              public fun optionalInt(default: Int): Int =
                  (instance.intent?.extras?.get("${"$"}{instance.packageName}.optionalInt") as? Int?) ?: default
            }
            
            public val OptionalsActivity.extras: OptionalsActivityParams
              get() = OptionalsActivityParams(this)
            """,
        )
    }

    @Test
    public fun `test activity generation with stubs`() {
        val (compilation, result) = compile(
            kotlin(
                "StubbedActivityStub.kt",
                """
                package com.juul.exercise.tests
                
                import com.juul.exercise.annotations.AsStub
                import com.juul.exercise.annotations.Exercise
                import com.juul.exercise.annotations.Extra
                
                @Exercise(Extra("stubbed", Int::class))
                @AsStub("com.juul.exercise.tests", "StubbedActivity")
                object StubbedActivityStub
                """,
            ),
            kotlin(
                "StubbedActivity.kt",
                """
                package com.juul.exercise.tests
                
                import android.app.Activity
                import com.juul.exercise.annotations.Exercise
                import com.juul.exercise.annotations.FromStub
                
                @Exercise
                @FromStub(StubbedActivityStub::class)
                class StubbedActivity : Activity()
                """,
            ),
        )
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val stubFile = getGeneratedFile(compilation, "StubbedActivityExerciseStubs.kt")
        assertThat(stubFile.readText().trim()).isEqualToKotlin(
            """
            package com.juul.exercise.tests
            
            import android.app.Activity
            import android.content.Context
            import android.content.Intent
            import android.os.Bundle
            import androidx.core.os.bundleOf
            import kotlin.Int
            import kotlin.String
            
            public fun bundleForStubbedActivity(context: Context, stubbed: Int): Bundle = bundleOf(
              "${"$"}{context.packageName}.stubbed" to stubbed
            )
            
            public fun bundleForStubbedActivity(packageName: String, stubbed: Int): Bundle = bundleOf(
              "${"$"}{packageName}.stubbed" to stubbed
            )
            
            public class StubbedActivityIntent : Intent {
              public constructor(context: Context, stubbed: Int) : super() {
                setClassName(context, "com.juul.exercise.tests.StubbedActivity")
                replaceExtras(bundleForStubbedActivity(
                  context.packageName,
                  stubbed
                ))
              }
            
              public constructor(packageName: String, stubbed: Int) : super() {
                setClassName(packageName, "com.juul.exercise.tests.StubbedActivity")
                replaceExtras(bundleForStubbedActivity(
                  packageName,
                  stubbed
                ))
              }
            }
            
            public class StubbedActivityParams(
              private val instance: Activity,
            ) {
              public val stubbed: Int
                get() = instance.intent?.extras?.get("${"$"}{instance.packageName}.stubbed") as Int
            }
            """,
        )

        val paramsFile = getGeneratedFile(compilation, "StubbedActivityExercise.kt")
        assertThat(paramsFile.readText().trim()).isEqualToKotlin(
            """
            package com.juul.exercise.tests
            
            public val StubbedActivity.extras: StubbedActivityParams
              get() = StubbedActivityParams(this)
            """,
        )
    }

    @Test
    public fun `test activity generation with parceler`() {
        val (compilation, result) = compile(
            kotlin(
                "ParcelerActivity.kt",
                """
                package com.juul.exercise.tests
                
                import android.app.Activity
                import android.os.Parcel
                import com.juul.exercise.annotations.Exercise
                import com.juul.exercise.annotations.Extra
                import kotlinx.android.parcel.Parceler
                
                data class ThirdPartyType(val value: String)
                
                object ThirdPartyTypeParceler : Parceler<ThirdPartyType> {
                    override fun create(parcel: Parcel) = ThirdPartyType(checkNotNull(parcel.readString()))
                    
                    override fun ThirdPartyType.write(parcel: Parcel, flags: Int) {
                        parcel.writeString(this.value)
                    }
                }
                
                @Exercise(
                    Extra("requiredValue", ThirdPartyType::class, parceler = ThirdPartyTypeParceler::class),
                    Extra("optionalValue", ThirdPartyType::class, optional = true, parceler = ThirdPartyTypeParceler::class)
                )
                class ParcelerActivity : Activity()
                """,
            ),
        )
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val file = getGeneratedFile(compilation, "ParcelerActivityExercise.kt")
        assertThat(file.readText().trim()).isEqualToKotlin(
            """
            package com.juul.exercise.tests

            import android.app.Activity
            import android.content.Context
            import android.content.Intent
            import android.os.Bundle
            import androidx.core.os.bundleOf
            import com.juul.exercise.runtime.createFromMarshalledBytes
            import com.juul.exercise.runtime.createFromMarshalledBytesOrNull
            import com.juul.exercise.runtime.writeToMarshalledBytes
            import com.juul.exercise.runtime.writeToMarshalledBytesOrNull
            import kotlin.ByteArray
            import kotlin.String
            
            public fun bundleForParcelerActivity(
              context: Context,
              requiredValue: ThirdPartyType,
              optionalValue: ThirdPartyType? = null,
            ): Bundle = bundleOf(
              "${"$"}{context.packageName}.requiredValue" to
                  ThirdPartyTypeParceler.writeToMarshalledBytes(requiredValue),
              "${"$"}{context.packageName}.optionalValue" to
                  ThirdPartyTypeParceler.writeToMarshalledBytesOrNull(optionalValue)
            )
            
            public fun bundleForParcelerActivity(
              packageName: String,
              requiredValue: ThirdPartyType,
              optionalValue: ThirdPartyType? = null,
            ): Bundle = bundleOf(
              "${"$"}{packageName}.requiredValue" to ThirdPartyTypeParceler.writeToMarshalledBytes(requiredValue),
              "${"$"}{packageName}.optionalValue" to
                  ThirdPartyTypeParceler.writeToMarshalledBytesOrNull(optionalValue)
            )

            public class ParcelerActivityIntent : Intent {
              public constructor(
                context: Context,
                requiredValue: ThirdPartyType,
                optionalValue: ThirdPartyType? = null,
              ) : super() {
                setClassName(context, "com.juul.exercise.tests.ParcelerActivity")
                replaceExtras(bundleForParcelerActivity(
                  context.packageName,
                  requiredValue,
                  optionalValue
                ))
              }
            
              public constructor(
                packageName: String,
                requiredValue: ThirdPartyType,
                optionalValue: ThirdPartyType? = null,
              ) : super() {
                setClassName(packageName, "com.juul.exercise.tests.ParcelerActivity")
                replaceExtras(bundleForParcelerActivity(
                  packageName,
                  requiredValue,
                  optionalValue
                ))
              }
            }
            
            public class ParcelerActivityParams(
              private val instance: Activity,
            ) {
              public val requiredValue: ThirdPartyType
                get() {
                  val data = instance.intent?.extras?.get("${"$"}{instance.packageName}.requiredValue") as ByteArray
                  return ThirdPartyTypeParceler.createFromMarshalledBytes(data)
                }
            
              public val optionalValue: ThirdPartyType?
                get() {
                  val data = instance.intent?.extras?.get("${"$"}{instance.packageName}.optionalValue") as ByteArray?
                  return ThirdPartyTypeParceler.createFromMarshalledBytesOrNull(data)
                }
            
              public fun optionalValue(default: ThirdPartyType): ThirdPartyType {
                val data = instance.intent?.extras?.get("${"$"}{instance.packageName}.optionalValue") as ByteArray?
                return ThirdPartyTypeParceler.createFromMarshalledBytesOrNull(data) ?: default
              }
            }
            
            public val ParcelerActivity.extras: ParcelerActivityParams
              get() = ParcelerActivityParams(this)
            """,
        )
    }
}
