package com.juul.exercise.compile

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.juul.exercise.annotations.Exercise
import com.juul.tuulbox.logging.Log

class ExerciseProcessor : SymbolProcessor {

    private lateinit var codeGenerator: CodeGenerator

    override fun init(
        options: Map<String, String>,
        kotlinVersion: KotlinVersion,
        codeGenerator: CodeGenerator,
        logger: KSPLogger
    ) {
        Log.dispatcher.install(KspTuulboxLogger(logger))
        this.codeGenerator = codeGenerator
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(checkNotNull(Exercise::class.qualifiedName))
            .asSequence()
            .filterIsInstance<KSClassDeclaration>()
        Log.verbose { "Resolved annotated symbols: $symbols" }
        for (symbol in symbols) {
            val pkg = symbol.packageName.asString()
            val file = codeGenerator.createNewFile(
                dependencies = Dependencies(aggregating = false, checkNotNull(symbol.containingFile)),
                packageName = pkg,
                fileName = "${symbol.simpleName.asString()}Exercise"
            )
            file.bufferedWriter(Charsets.UTF_8).use { writer ->
                writer.write("package $pkg")
            }
        }
        return emptyList()
    }
}
