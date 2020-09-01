# Exercise

Exercise is an annotation processor for Kotlin Android projects.
Exercise makes it possible to pass intent extras easily and correctly.

# Getting started

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.juul.exercise/compile/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.juul.exercise/compile)

First, add it to gradle.

```gradle
repositories {
  jcenter() // or mavenCentral()
}

dependencies {
  implementation "com.juul.exercise:annotations:$version"
  implementation "com.juul.exercise:runtime:$version"
  kapt "com.juul.exercise:compile:$version"
}
```

Then, create your activity.

```kotlin
@Exercise(Extra("someNumber", Int::class))
class YourActivity : AppCompatActivity() {
  // ...

  fun useExtras(): Int = 2 * extras.someNumber
}
```

Finally, navigate to it.

```kotlin
class FromActivity : AppCompatActivity() {
  // ...

  fun navigate() {
    startActivity(YourActivityIntent(this, someNumber = 25))
  }
}
```

## Nullables and Optionals

To make an `Extra` nullable, you mark it as `optional`. Declaration and use:

```kotlin
@Exercise(Extra("anyString", String::class, optional = true))
class OptionalsActivity : AppCompatActivity() {
  // ...

  fun useExtras() {
    println(extras.anyString) // nullable
    println(extras.anyString(default = "someDefault")) // non-null with function call
  }
}
```

Creating the intent:

```kotlin
// Works with a nullable argument
fun getString(): String? = TODO()
OptionalsActivityIntent(context, anyString = getString())

// Argument has a default value of null, so it can be left off.
OptionalsActivityIntent(context)
// The above is same as the below
OptionalsActivityIntent(context, anyString = null)
```

## Parcelers

Exercise works with KotlinX [`Parceler`](https://kotlinlang.org/docs/reference/compiler-plugins.html#custom-parcelers) The following example assumes you are consuming some external code:

```kotlin
data class ThirdPartyType(val value: String)
```

First, write a `Parceler` just like you would for a `@Parcelize` class (or reuse an existing one).

```kotlin
object ThirdPartyTypeParceler : Parceler<ThirdPartyType> {
  override fun create(parcel: Parcel) = ThirdPartyType(checkNotNull(parcel.readString()))

  override fun ThirdPartyType.write(parcel: Parcel, flags: Int) {
    parcel.writeString(this.value)
  }
}
```

Declaration and use:

```kotlin
@Exercise(Extra("myThirdPartyType", ThirdPartyType::class, parceler = ThirdPartyTypeParceler::class))
class ThirdPartyTypeActivity : AppCompatActivity() {
  // ...

  fun useExtras() {
    println(extras.myThirdPartyType)
  }
}
```

Creating the intent:

```kotlin
ThirdPartyTypeActivityIntent(context, myThirdPartyType = ThirdPartyType("Some string"))
```

## Services

Exercise supports intents for a `Service` much like an `Activity`.

```kotlin
@Exercise(Extra("someNumber", Int::class))
class YourService : JobIntentService() {
    override fun onHandleWork(intent: Intent) {
        println("Some Number: ${extras(intent).someNumber}")
    }
}
```

Creating the intent:

```kotlin
val intent = YourServiceIntent(context, someNumber = 5)
```

Launching a `JobIntentService`:

```kotlin
JobIntentService.enqueueWork(context, YourService::class, jobId, intent)
```

Launching other `Service`s:

```kotlin
context.startService(intent)
```

# License

```
Copyright 2020 JUUL Labs, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
