import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import java.net.URI

buildscript {
    repositories {
        mavenCentral()
        google()
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
    // TODO: Enable this
    // id("binary-compatibility-validator") version "0.2.3"
    id("org.jetbrains.dokka") version "1.4.10.2" apply false
    id("com.vanniktech.maven.publish") version "0.13.0" apply false
    id("net.mbonnin.one.eight") version "0.1"
}

subprojects {
    repositories {
        google()
        jcenter()
        maven(url = "https://kotlin.bintray.com/kotlinx/")
        maven {
            url = URI("https://maven.pkg.github.com/juullabs/android-github-packages")
            credentials {
                username = findProperty("github.packages.username") as? String
                password = findProperty("github.packages.password") as? String
            }
        }
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
