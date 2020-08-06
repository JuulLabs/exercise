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
