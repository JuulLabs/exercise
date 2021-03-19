plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-android-extensions")
    // TODO:
    // apply plugin: "com.vanniktech.maven.publish"
    // apply from: rootProject.file('gradle/jacoco-android.gradle')
}

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
