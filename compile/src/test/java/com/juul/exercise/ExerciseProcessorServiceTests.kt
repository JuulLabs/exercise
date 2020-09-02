package com.juul.exercise

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile.Companion.kotlin
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ExerciseProcessorServiceTests : ExerciseProcessorTests() {

    @Test
    fun `test service generation with no extras`() {
        val result = compile(
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
                """
            )
        )
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val file = result.getGeneratedFile("NoExtrasServiceExercise.kt")
        assertThat(file.readText().trim()).isEqualToKotlin(
            """
            package com.juul.exercise.tests
            
            import android.content.Context
            import android.content.Intent
            import kotlin.String
            
            class NoExtrasServiceIntent : Intent {
              constructor(context: Context) : super() {
                setClassName(context, "com.juul.exercise.tests.NoExtrasService")
              }
            
              constructor(packageName: String) : super() {
                setClassName(packageName, "com.juul.exercise.tests.NoExtrasService")
              }
            }
            
            class NoExtrasServiceParams(
              private val instance: NoExtrasService,
              private val intent: Intent
            )
            
            fun NoExtrasService.extras(intent: Intent): NoExtrasServiceParams = NoExtrasServiceParams(this,
                intent)
            """
        )
    }

    @Test
    fun `test service generation with abstract superclass and subclass`() {
        val result = compile(
            kotlin(
                "SuperclassService.kt",
                """
                package com.juul.exercise.tests
                
                import android.app.IntentService
                import com.juul.exercise.annotations.Exercise
                import com.juul.exercise.annotations.Extra
                
                @Exercise(Extra("fromSuperclass", Int::class))
                abstract class SuperclassService : IntentService("test")
                """
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
                """
            )
        )
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val superclassFile = result.getGeneratedFile("SuperclassServiceExercise.kt")
        assertThat(superclassFile.readText().trim()).isEqualToKotlin(
            """
            package com.juul.exercise.tests
            
            import android.content.Intent
            import kotlin.Int
            
            class SuperclassServiceParams(
              private val instance: SuperclassService,
              private val intent: Intent
            ) {
              val fromSuperclass: Int
                get() = intent.extras?.get("${"$"}{instance.packageName}.fromSuperclass") as Int
            }
            
            fun SuperclassService.extras(intent: Intent): SuperclassServiceParams =
                SuperclassServiceParams(this, intent)
            """
        )

        val subclassFile = result.getGeneratedFile("SubclassServiceExercise.kt")
        assertThat(subclassFile.readText().trim()).isEqualToKotlin(
            """
            package com.juul.exercise.tests
            
            import android.content.Context
            import android.content.Intent
            import androidx.core.os.bundleOf
            import kotlin.Int
            import kotlin.String
            
            class SubclassServiceIntent : Intent {
              constructor(
                context: Context,
                fromSuperclass: Int,
                fromSubclass: String
              ) : super() {
                setClassName(context, "com.juul.exercise.tests.SubclassService")
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
                setClassName(packageName, "com.juul.exercise.tests.SubclassService")
                replaceExtras(bundleOf(
                  "${"$"}{packageName}.fromSuperclass" to fromSuperclass,
                  "${"$"}{packageName}.fromSubclass" to fromSubclass
                ))
              }
            }
            
            class SubclassServiceParams(
              private val instance: SubclassService,
              private val intent: Intent
            ) {
              val fromSuperclass: Int
                get() = intent.extras?.get("${"$"}{instance.packageName}.fromSuperclass") as Int
            
              val fromSubclass: String
                get() = intent.extras?.get("${"$"}{instance.packageName}.fromSubclass") as String
            }
            
            fun SubclassService.extras(intent: Intent): SubclassServiceParams = SubclassServiceParams(this,
                intent)
            """
        )
    }

    @Test
    fun `test service generation with generics`() {
        val result = compile(
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
                """
            )
        )
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val file = result.getGeneratedFile("ListServiceExercise.kt")
        assertThat(file.readText().trim()).isEqualToKotlin(
            """
            package com.juul.exercise.tests
            
            import android.content.Context
            import android.content.Intent
            import androidx.core.os.bundleOf
            import kotlin.Int
            import kotlin.String
            import kotlin.Suppress
            import kotlin.collections.List
            
            class ListServiceIntent : Intent {
              constructor(context: Context, listOfInt: List<Int>) : super() {
                setClassName(context, "com.juul.exercise.tests.ListService")
                replaceExtras(bundleOf(
                  "${"$"}{context.packageName}.listOfInt" to listOfInt
                ))
              }
            
              constructor(packageName: String, listOfInt: List<Int>) : super() {
                setClassName(packageName, "com.juul.exercise.tests.ListService")
                replaceExtras(bundleOf(
                  "${"$"}{packageName}.listOfInt" to listOfInt
                ))
              }
            }
            
            class ListServiceParams(
              private val instance: ListService,
              private val intent: Intent
            ) {
              val listOfInt: List<Int>
                @Suppress("UNCHECKED_CAST")
                get() = intent.extras?.get("${"$"}{instance.packageName}.listOfInt") as List<Int>
            }
            
            fun ListService.extras(intent: Intent): ListServiceParams = ListServiceParams(this, intent)
            """
        )
    }

    @Test
    fun `test service generation with optionals`() {
        val result = compile(
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
                """
            )
        )
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val file = result.getGeneratedFile("OptionalsServiceExercise.kt")
        assertThat(file.readText().trim()).isEqualToKotlin(
            """
            package com.juul.exercise.tests
            
            import android.content.Context
            import android.content.Intent
            import androidx.core.os.bundleOf
            import kotlin.Int
            import kotlin.String
            
            class OptionalsServiceIntent : Intent {
              constructor(context: Context, optionalInt: Int? = null) : super() {
                setClassName(context, "com.juul.exercise.tests.OptionalsService")
                replaceExtras(bundleOf(
                  "${"$"}{context.packageName}.optionalInt" to optionalInt
                ))
              }
            
              constructor(packageName: String, optionalInt: Int? = null) : super() {
                setClassName(packageName, "com.juul.exercise.tests.OptionalsService")
                replaceExtras(bundleOf(
                  "${"$"}{packageName}.optionalInt" to optionalInt
                ))
              }
            }
            
            class OptionalsServiceParams(
              private val instance: OptionalsService,
              private val intent: Intent
            ) {
              val optionalInt: Int?
                get() = intent.extras?.get("${"$"}{instance.packageName}.optionalInt") as Int?
            
              fun optionalInt(default: Int): Int = (intent.extras?.get("${"$"}{instance.packageName}.optionalInt")
                  as? Int?) ?: default
            }
            
            fun OptionalsService.extras(intent: Intent): OptionalsServiceParams = OptionalsServiceParams(this,
                intent)
            """
        )
    }

    @Test
    fun `test service generation with parceler`() {
        val result = compile(
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
                """
            )
        )
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val file = result.getGeneratedFile("ParcelerServiceExercise.kt")
        assertThat(file.readText().trim()).isEqualToKotlin(
            """
            package com.juul.exercise.tests

            import android.content.Context
            import android.content.Intent
            import androidx.core.os.bundleOf
            import com.juul.exercise.runtime.createFromMarshalledBytes
            import com.juul.exercise.runtime.createFromMarshalledBytesOrNull
            import com.juul.exercise.runtime.writeToMarshalledBytes
            import com.juul.exercise.runtime.writeToMarshalledBytesOrNull
            import kotlin.ByteArray
            import kotlin.String
            
            class ParcelerServiceIntent : Intent {
              constructor(
                context: Context,
                requiredValue: ThirdPartyType,
                optionalValue: ThirdPartyType? = null
              ) : super() {
                setClassName(context, "com.juul.exercise.tests.ParcelerService")
                replaceExtras(bundleOf(
                  "${"$"}{context.packageName}.requiredValue" to
                      ThirdPartyTypeParceler.writeToMarshalledBytes(requiredValue),
                  "${"$"}{context.packageName}.optionalValue" to
                      ThirdPartyTypeParceler.writeToMarshalledBytesOrNull(optionalValue)
                ))
              }
            
              constructor(
                packageName: String,
                requiredValue: ThirdPartyType,
                optionalValue: ThirdPartyType? = null
              ) : super() {
                setClassName(packageName, "com.juul.exercise.tests.ParcelerService")
                replaceExtras(bundleOf(
                  "${"$"}{packageName}.requiredValue" to
                      ThirdPartyTypeParceler.writeToMarshalledBytes(requiredValue),
                  "${"$"}{packageName}.optionalValue" to
                      ThirdPartyTypeParceler.writeToMarshalledBytesOrNull(optionalValue)
                ))
              }
            }
            
            class ParcelerServiceParams(
              private val instance: ParcelerService,
              private val intent: Intent
            ) {
              val requiredValue: ThirdPartyType
                get() {
                  val data = intent.extras?.get("${"$"}{instance.packageName}.requiredValue") as ByteArray
                  return ThirdPartyTypeParceler.createFromMarshalledBytes(data)
                }
            
              val optionalValue: ThirdPartyType?
                get() {
                  val data = intent.extras?.get("${"$"}{instance.packageName}.optionalValue") as ByteArray?
                  return ThirdPartyTypeParceler.createFromMarshalledBytesOrNull(data)
                }
            
              fun optionalValue(default: ThirdPartyType): ThirdPartyType {
                val data = intent.extras?.get("${"$"}{instance.packageName}.optionalValue") as ByteArray?
                return ThirdPartyTypeParceler.createFromMarshalledBytesOrNull(data) ?: default
              }
            }
            
            fun ParcelerService.extras(intent: Intent): ParcelerServiceParams = ParcelerServiceParams(this,
                intent)
            """
        )
    }
}
