plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-parcelize")
    id("org.jmailen.kotlinter")
    jacoco
    id("com.vanniktech.maven.publish")
}

apply(from = rootProject.file("gradle/jacoco.gradle.kts"))

android {
    compileSdkVersion(AndroidSdk.Compile)

    defaultConfig {
        minSdkVersion(AndroidSdk.Minimum)
        targetSdkVersion(AndroidSdk.Target)
    }
}

dependencies {
    testImplementation(assertj("core"))
    testImplementation(kotlin("test-junit"))
    testImplementation(robolectric())
}
