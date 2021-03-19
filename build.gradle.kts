import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL

buildscript {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.4.30"))
    }
}

plugins {
    kotlin("jvm") version "1.4.30" apply false
    id("com.android.application") version "4.0.0" apply false
    id("com.android.library") version "4.0.0" apply false
    id("org.jmailen.kotlinter") version "3.2.0" apply false
    id("binary-compatibility-validator") version "0.2.3"
    id("org.jetbrains.dokka") version "1.4.30" apply false
    id("com.vanniktech.maven.publish") version "0.13.0" apply false
    id("net.mbonnin.one.eight") version "0.2"
}

subprojects {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }

    tasks.withType<Test>().configureEach {
        testLogging {
            events("started", "passed", "skipped", "failed", "standardOut", "standardError")
            exceptionFormat = FULL
            showExceptions = true
            showStackTraces = true
            showCauses = true
        }
    }
}
