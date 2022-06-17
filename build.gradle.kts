buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        // Workaround for:
        // > Incompatible version of Kotlin metadata.
        // > Maximal supported Kotlin metadata version: 1.5.1,
        // >  com/juul/exercise/annotations/AsStub Kotlin metadata version: 1.7.1.
        // > As a workaround, it is possible to manually update 'kotlinx-metadata-jvm' version in your project.
        //
        // todo: Remove when binary-compatibility-validator bundles support for metadata 1.7.x.
        classpath("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.4.2")
    }
}

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlinter) apply false
    alias(libs.plugins.validator)
    alias(libs.plugins.dokka)
    alias(libs.plugins.maven.publish) apply false
    alias(libs.plugins.one.eight)
}

allprojects {
    repositories {
        mavenCentral()
        google()
    }

    tasks.withType<Test>().configureEach {
        testLogging {
            events("started", "passed", "skipped", "failed", "standardOut", "standardError")
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
            showExceptions = true
            showStackTraces = true
            showCauses = true
        }
    }
}
