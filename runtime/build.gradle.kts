plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-parcelize")
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
