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
    compileSdk = libs.versions.android.compile.get().toInt()
    defaultConfig.minSdk = libs.versions.android.min.get().toInt()
    namespace = "com.juul.exercise.runtime"
}

dependencies {
    api(libs.kotlin.parcelize.runtime)
    testImplementation(libs.assertj)
    testImplementation(kotlin("test-junit"))
    testImplementation(libs.robolectric)
}
