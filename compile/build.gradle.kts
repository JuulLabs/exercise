plugins {
    kotlin("jvm")
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
