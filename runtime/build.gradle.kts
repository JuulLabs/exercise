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
    testImplementation("org.assertj:assertj-core:3.15.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.4.30")
    testImplementation("org.robolectric:robolectric:4.3.1")
}
