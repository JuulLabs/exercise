plugins {
    kotlin("jvm")
    id("com.vanniktech.maven.publish")
}

kotlin {
    jvmToolchain(libs.versions.jvm.toolchain.get().toInt())
}
