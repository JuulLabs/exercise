package com.juul.exercise.compile

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.juul.exercise.annotations.Exercise
import com.juul.exercise.compile.read.asReceiver
import com.juul.exercise.compile.read.findDependencies
import com.juul.exercise.compile.read.findParameters
import com.juul.exercise.compile.write.ExerciseWriter
import com.juul.tuulbox.logging.Log

public class ExerciseProcessor : SymbolProcessor {

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
            .toList()
        if (symbols.isNotEmpty()) {
            Log.info { "Found @Exercise annotated classes: ${symbols.joinToString()}" }
            for (symbol in symbols) {
                processClass(symbol)
            }
        }
        return emptyList()
    }

    private fun processClass(symbol: KSClassDeclaration) {
        val logSymbol = symbol.loggable()
        Log.debug(logSymbol) { "Start processing file." }
        try {
            val dependencies = symbol.findDependencies()
            Log.verbose(logSymbol) { "Found dependencies: ${dependencies.originatingFiles.joinToString()}" }
            val parameters = symbol.findParameters()
            Log.verbose(logSymbol) { "Found parameters: ${symbol.findParameters()}" }
            val receiver = symbol.asReceiver()
            Log.verbose(logSymbol) { "Using receiver: $receiver" }
            val writer = ExerciseWriter(receiver, parameters)
            val fileSpec = writer.generateFileSpec()
            codeGenerator.createNewFile(dependencies, fileSpec.packageName, fileSpec.name).use { stream ->
                stream.bufferedWriter(Charsets.UTF_8).use { bufferedStream ->
                    fileSpec.writeTo(bufferedStream)
                }
            }
        } catch (t: Throwable) {
            Log.error(t.withNode(symbol)) { "An exception was thrown during processing." }
        }
        Log.debug(logSymbol) { "Finished processing file." }
    }
}
