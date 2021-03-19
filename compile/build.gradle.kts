import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

kotlin {
    explicitApi()
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}

dependencies {
    implementation(project(":annotations"))
    implementation(project(":stubs"))
    implementation(kotlinPoet())
    implementation(kotlinSymbolProcessing())
    implementation(tuulbox("logging"))
    testImplementation(kotlin("test-junit"))
    testImplementation(assertj("core"))
    testImplementation(kotlinCompileTesting("ksp"))
}
