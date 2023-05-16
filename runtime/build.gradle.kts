plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-parcelize")
    id("org.jmailen.kotlinter")
    jacoco
    id("org.jetbrains.dokka")
    id("com.vanniktech.maven.publish")
}

apply(from = rootProject.file("gradle/jacoco.gradle.kts"))

kotlin {
    jvmToolchain(libs.versions.jvm.toolchain.get().toInt())
}

android {
    // Workaround (for `jvmToolchain` not being honored) needed until AGP 8.1.0-alpha09.
    // https://kotlinlang.org/docs/gradle-configure-project.html#gradle-java-toolchains-support
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    compileSdkVersion(libs.versions.android.compile.get())

    defaultConfig {
        minSdkVersion(libs.versions.android.min.get())
    }

    namespace = "com.juul.exercise.runtime"
}

dependencies {
    api(libs.kotlin.parcelize.runtime)
    testImplementation(libs.assertj)
    testImplementation(kotlin("test-junit"))
    testImplementation(libs.robolectric)
}
