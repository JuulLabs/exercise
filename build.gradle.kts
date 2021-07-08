import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL

buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    kotlin("jvm") version "1.5.10" apply false
    id("com.android.application") version "4.2.0" apply false
    id("com.android.library") version "4.2.0" apply false
    id("org.jmailen.kotlinter") version "3.4.5" apply false
    id("binary-compatibility-validator") version "0.6.0"
    id("org.jetbrains.dokka") version "1.5.0" apply false
    id("com.vanniktech.maven.publish") version "0.17.0" apply false
    id("net.mbonnin.one.eight") version "0.2"
}

subprojects {
    repositories {
        mavenCentral()
        google()
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
