package com.juul.exercise.compile

import com.juul.exercise.compile.extensions.getGeneratedFile
import com.juul.exercise.compile.extensions.isEqualToKotlin
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile.Companion.kotlin
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

public class ExerciseProcessorServiceTests : ExerciseProcessorTests() {

    @Test
    public fun `test service generation with no extras`() {
        val (compilation, result) = compile(
            kotlin(
                "NoExtrasService.kt",
                """
                package com.juul.exercise.tests
                
                import android.app.IntentService
                import android.content.Intent
                import com.juul.exercise.annotations.Exercise
                
                @Exercise
                class NoExtrasService : IntentService("test") {
                    override fun onHandleIntent(intent: Intent?) { TODO() }
                }
                """,
            ),
        )
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val file = getGeneratedFile(compilation, "NoExtrasServiceExercise.kt")
        assertThat(file.readText().trim()).isEqualToKotlin(
            """
            package com.juul.exercise.tests

            import android.content.Context
            import android.content.Intent
            import android.os.Bundle
            import androidx.core.os.bundleOf
            import kotlin.String

            public fun bundleForNoExtrasService(context: Context): Bundle = bundleOf()

            public fun bundleForNoExtrasService(packageName: String): Bundle = bundleOf()
            
            public class NoExtrasServiceIntent : Intent {
              public constructor(context: Context) : super() {
                setClassName(context, "com.juul.exercise.tests.NoExtrasService")
              }
            
              public constructor(packageName: String) : super() {
                setClassName(packageName, "com.juul.exercise.tests.NoExtrasService")
              }
            }
            
            public class NoExtrasServiceParams(
              private val instance: NoExtrasService,
              private val intent: Intent,
            )
            
            public fun NoExtrasService.extras(intent: Intent): NoExtrasServiceParams =
                NoExtrasServiceParams(this, intent)
            """,
        )
    }

    @Test
    public fun `test service generation with abstract superclass and subclass`() {
        val (compilation, result) = compile(
            kotlin(
                "SuperclassService.kt",
                """
                package com.juul.exercise.tests
                
                import android.app.IntentService
                import com.juul.exercise.annotations.Exercise
                import com.juul.exercise.annotations.Extra
                
                @Exercise(Extra("fromSuperclass", Int::class))
                abstract class SuperclassService : IntentService("test")
                """,
            ),
            kotlin(
                "SubclassService.kt",
                """
                package com.juul.exercise.tests
                
                import android.content.Intent
                import com.juul.exercise.annotations.Exercise
                import com.juul.exercise.annotations.Extra
                
                @Exercise(Extra("fromSubclass", String::class))
                class SubclassService : SuperclassService() {
                    override fun onHandleIntent(intent: Intent?) { TODO() }
                }
                """,
            ),
        )
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val superclassFile = getGeneratedFile(compilation, "SuperclassServiceExercise.kt")
        assertThat(superclassFile.readText().trim()).isEqualToKotlin(
            """
            package com.juul.exercise.tests
            
            import android.content.Intent
            import kotlin.Int
            
            public class SuperclassServiceParams(
              private val instance: SuperclassService,
              private val intent: Intent,
            ) {
              public val fromSuperclass: Int
                get() = intent.extras?.get("${"$"}{instance.packageName}.fromSuperclass") as Int
            }
            
            public fun SuperclassService.extras(intent: Intent): SuperclassServiceParams =
                SuperclassServiceParams(this, intent)
            """,
        )

        val subclassFile = getGeneratedFile(compilation, "SubclassServiceExercise.kt")
        assertThat(subclassFile.readText().trim()).isEqualToKotlin(
            """
            package com.juul.exercise.tests
            
            import android.content.Context
            import android.content.Intent
            import android.os.Bundle
            import androidx.core.os.bundleOf
            import kotlin.Int
            import kotlin.String

            public fun bundleForSubclassService(
              context: Context,
              fromSuperclass: Int,
              fromSubclass: String,
            ): Bundle = bundleOf(
              "${"$"}{context.packageName}.fromSuperclass" to fromSuperclass,
              "${"$"}{context.packageName}.fromSubclass" to fromSubclass
            )

            public fun bundleForSubclassService(
              packageName: String,
              fromSuperclass: Int,
              fromSubclass: String,
            ): Bundle = bundleOf(
              "${"$"}{packageName}.fromSuperclass" to fromSuperclass,
              "${"$"}{packageName}.fromSubclass" to fromSubclass
            )

            public class SubclassServiceIntent : Intent {
              public constructor(
                context: Context,
                fromSuperclass: Int,
                fromSubclass: String,
              ) : super() {
                setClassName(context, "com.juul.exercise.tests.SubclassService")
                replaceExtras(bundleForSubclassService(
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
                setClassName(packageName, "com.juul.exercise.tests.SubclassService")
                replaceExtras(bundleForSubclassService(
                  packageName,
                  fromSuperclass,
                  fromSubclass
                ))
              }
            }
            
            public class SubclassServiceParams(
              private val instance: SubclassService,
              private val intent: Intent,
            ) {
              public val fromSuperclass: Int
                get() = intent.extras?.get("${"$"}{instance.packageName}.fromSuperclass") as Int
            
              public val fromSubclass: String
                get() = intent.extras?.get("${"$"}{instance.packageName}.fromSubclass") as String
            }
            
            public fun SubclassService.extras(intent: Intent): SubclassServiceParams =
                SubclassServiceParams(this, intent)
            """,
        )
    }

    @Test
    public fun `test service generation with generics`() {
        val (compilation, result) = compile(
            kotlin(
                "ListService.kt",
                """
                package com.juul.exercise.tests
                
                import android.app.IntentService
                import android.content.Intent
                import com.juul.exercise.annotations.Exercise
                import com.juul.exercise.annotations.Extra
                
                @Exercise(Extra("listOfInt", List::class, Int::class))
                class ListService : IntentService("test") {
                    override fun onHandleIntent(intent: Intent?) { TODO() }
                }
                """,
            ),
        )
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val file = getGeneratedFile(compilation, "ListServiceExercise.kt")
        assertThat(file.readText().trim()).isEqualToKotlin(
            """
            package com.juul.exercise.tests
            
            import android.content.Context
            import android.content.Intent
            import android.os.Bundle
            import androidx.core.os.bundleOf
            import kotlin.Int
            import kotlin.String
            import kotlin.Suppress
            import kotlin.collections.List

            public fun bundleForListService(context: Context, listOfInt: List<Int>): Bundle = bundleOf(
              "${"$"}{context.packageName}.listOfInt" to listOfInt
            )
            
            public fun bundleForListService(packageName: String, listOfInt: List<Int>): Bundle = bundleOf(
              "${"$"}{packageName}.listOfInt" to listOfInt
            )

            public class ListServiceIntent : Intent {
              public constructor(context: Context, listOfInt: List<Int>) : super() {
                setClassName(context, "com.juul.exercise.tests.ListService")
                replaceExtras(bundleForListService(
                  context.packageName,
                  listOfInt
                ))
              }
            
              public constructor(packageName: String, listOfInt: List<Int>) : super() {
                setClassName(packageName, "com.juul.exercise.tests.ListService")
                replaceExtras(bundleForListService(
                  packageName,
                  listOfInt
                ))
              }
            }
            
            public class ListServiceParams(
              private val instance: ListService,
              private val intent: Intent,
            ) {
              public val listOfInt: List<Int>
                @Suppress("UNCHECKED_CAST")
                get() = intent.extras?.get("${"$"}{instance.packageName}.listOfInt") as List<Int>
            }
            
            public fun ListService.extras(intent: Intent): ListServiceParams = ListServiceParams(this, intent)
            """,
        )
    }

    @Test
    public fun `test service generation with optionals`() {
        val (compilation, result) = compile(
            kotlin(
                "OptionalsService.kt",
                """
                package com.juul.exercise.tests
                
                import android.app.IntentService
                import android.content.Intent
                import com.juul.exercise.annotations.Exercise
                import com.juul.exercise.annotations.Extra
                
                @Exercise(Extra("optionalInt", Int::class, optional = true))
                class OptionalsService : IntentService("test") {
                    override fun onHandleIntent(intent: Intent?) { TODO() }
                }
                """,
            ),
        )
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val file = getGeneratedFile(compilation, "OptionalsServiceExercise.kt")
        assertThat(file.readText().trim()).isEqualToKotlin(
            """
            package com.juul.exercise.tests
            
            import android.content.Context
            import android.content.Intent
            import android.os.Bundle
            import androidx.core.os.bundleOf
            import kotlin.Int
            import kotlin.String
            
            public fun bundleForOptionalsService(context: Context, optionalInt: Int? = null): Bundle = bundleOf(
              "${"$"}{context.packageName}.optionalInt" to optionalInt
            )
            
            public fun bundleForOptionalsService(packageName: String, optionalInt: Int? = null): Bundle =
                bundleOf(
              "${"$"}{packageName}.optionalInt" to optionalInt
            )
            
            public class OptionalsServiceIntent : Intent {
              public constructor(context: Context, optionalInt: Int? = null) : super() {
                setClassName(context, "com.juul.exercise.tests.OptionalsService")
                replaceExtras(bundleForOptionalsService(
                  context.packageName,
                  optionalInt
                ))
              }
            
              public constructor(packageName: String, optionalInt: Int? = null) : super() {
                setClassName(packageName, "com.juul.exercise.tests.OptionalsService")
                replaceExtras(bundleForOptionalsService(
                  packageName,
                  optionalInt
                ))
              }
            }
            
            public class OptionalsServiceParams(
              private val instance: OptionalsService,
              private val intent: Intent,
            ) {
              public val optionalInt: Int?
                get() = intent.extras?.get("${"$"}{instance.packageName}.optionalInt") as Int?
            
              public fun optionalInt(default: Int): Int =
                  (intent.extras?.get("${"$"}{instance.packageName}.optionalInt") as? Int?) ?: default
            }
            
            public fun OptionalsService.extras(intent: Intent): OptionalsServiceParams =
                OptionalsServiceParams(this, intent)
            """,
        )
    }

    @Test
    public fun `test service generation with parceler`() {
        val (compilation, result) = compile(
            kotlin(
                "ParcelerService.kt",
                """
                package com.juul.exercise.tests
                
                import android.app.IntentService
                import android.content.Intent
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
                class ParcelerService : IntentService("test") {
                    override fun onHandleIntent(intent: Intent?) { TODO() }
                }
                """,
            ),
        )
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val file = getGeneratedFile(compilation, "ParcelerServiceExercise.kt")
        assertThat(file.readText().trim()).isEqualToKotlin(
            """
            package com.juul.exercise.tests

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

            public fun bundleForParcelerService(
              context: Context,
              requiredValue: ThirdPartyType,
              optionalValue: ThirdPartyType? = null,
            ): Bundle = bundleOf(
              "${"$"}{context.packageName}.requiredValue" to
                  ThirdPartyTypeParceler.writeToMarshalledBytes(requiredValue),
              "${"$"}{context.packageName}.optionalValue" to
                  ThirdPartyTypeParceler.writeToMarshalledBytesOrNull(optionalValue)
            )
            
            public fun bundleForParcelerService(
              packageName: String,
              requiredValue: ThirdPartyType,
              optionalValue: ThirdPartyType? = null,
            ): Bundle = bundleOf(
              "${"$"}{packageName}.requiredValue" to ThirdPartyTypeParceler.writeToMarshalledBytes(requiredValue),
              "${"$"}{packageName}.optionalValue" to
                  ThirdPartyTypeParceler.writeToMarshalledBytesOrNull(optionalValue)
            )

            public class ParcelerServiceIntent : Intent {
              public constructor(
                context: Context,
                requiredValue: ThirdPartyType,
                optionalValue: ThirdPartyType? = null,
              ) : super() {
                setClassName(context, "com.juul.exercise.tests.ParcelerService")
                replaceExtras(bundleForParcelerService(
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
                setClassName(packageName, "com.juul.exercise.tests.ParcelerService")
                replaceExtras(bundleForParcelerService(
                  packageName,
                  requiredValue,
                  optionalValue
                ))
              }
            }
            
            public class ParcelerServiceParams(
              private val instance: ParcelerService,
              private val intent: Intent,
            ) {
              public val requiredValue: ThirdPartyType
                get() {
                  val data = intent.extras?.get("${"$"}{instance.packageName}.requiredValue") as ByteArray
                  return ThirdPartyTypeParceler.createFromMarshalledBytes(data)
                }
            
              public val optionalValue: ThirdPartyType?
                get() {
                  val data = intent.extras?.get("${"$"}{instance.packageName}.optionalValue") as ByteArray?
                  return ThirdPartyTypeParceler.createFromMarshalledBytesOrNull(data)
                }
            
              public fun optionalValue(default: ThirdPartyType): ThirdPartyType {
                val data = intent.extras?.get("${"$"}{instance.packageName}.optionalValue") as ByteArray?
                return ThirdPartyTypeParceler.createFromMarshalledBytesOrNull(data) ?: default
              }
            }
            
            public fun ParcelerService.extras(intent: Intent): ParcelerServiceParams =
                ParcelerServiceParams(this, intent)
            """,
        )
    }
}
