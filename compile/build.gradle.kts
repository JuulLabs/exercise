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
}

dependencies {
    implementation(libs.khronicle)
    implementation(libs.kotlinpoet)
    implementation(libs.ksp.api)
    implementation(project(":annotations"))
    implementation(project(":stubs"))
    testImplementation(kotlin("test-junit"))
    testImplementation(libs.assertj)
    testImplementation(libs.ksp.testing)
}
