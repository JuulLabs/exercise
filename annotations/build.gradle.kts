plugins {
    kotlin("jvm")
    id("org.jmailen.kotlinter")
    jacoco
    id("org.jetbrains.dokka")
    id("com.vanniktech.maven.publish")
}

apply(from = rootProject.file("gradle/jacoco.gradle.kts"))

kotlin {
    jvmToolchain(libs.versions.jvm.toolchain.get().toInt())
}
