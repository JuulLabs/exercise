fun assertj(
    module: String,
    version: String = "3.19.0"
) = "org.assertj:assertj-$module:$version"

fun kotlinCompileTesting(
    submodule: String? = null,
    version: String = "1.3.6"
) = when (submodule) {
    null -> "com.github.tschuchortdev:kotlin-compile-testing:$version"
    else -> "com.github.tschuchortdev:kotlin-compile-testing-$submodule:$version"
}

fun kotlinPoet(
    version: String = "1.7.2"
) = "com.squareup:kotlinpoet:$version"

fun kotlinSymbolProcessing(
    version: String = "1.4.30-1.0.0-alpha05"
) = "com.google.devtools.ksp:symbol-processing-api:$version"

fun tuulbox(
    module: String,
    version: String = "3.2.0"
) = "com.juul.tuulbox:$module:$version"
