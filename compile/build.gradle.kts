plugins {
    kotlin("jvm")
    id("org.jmailen.kotlinter")
    jacoco
    id("com.vanniktech.maven.publish")
}

apply(from = rootProject.file("gradle/jacoco.gradle.kts"))

kotlin {
    explicitApi()
    jvmToolchain(libs.versions.jvm.toolchain.get().toInt())
    sourceSets.all {
        languageSettings.optIn("org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi")
    }
}

dependencies {
    implementation(project(":annotations"))
    implementation(project(":stubs"))
    implementation(libs.kotlinpoet)
    implementation(libs.ksp.api)
    implementation(libs.tuulbox.logging)
    testImplementation(kotlin("test-junit"))
    testImplementation(libs.assertj)
    testImplementation(libs.ksp.testing)
}
