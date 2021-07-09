package com.juul.exercise.compile

import com.google.devtools.ksp.processing.CodeGenerator
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

internal class ExerciseProcessor(
    private val codeGenerator: CodeGenerator
) : SymbolProcessor {

    private var isFirstRound = true

    override fun process(resolver: Resolver): List<KSAnnotated> {
        // FIXME: Hypothetically, somebody could write a KSP generator which outputs @Exercise annotated classes. This
        //        won't work as long as we have this shortcut in place. That said, this shortcut works around a crasher
        //        where we see `NoDescriptorForDeclarationException: Descriptor wasn't found for declaration CLASS`
        if (!isFirstRound) return emptyList()

        val symbols = resolver.getSymbolsWithAnnotation(Exercise::class.java.name)
            .filterIsInstance<KSClassDeclaration>()
        if (symbols.any()) {
            Log.info { "Found @Exercise annotated classes: ${symbols.joinToString()}" }
            for (symbol in symbols) {
                processClass(symbol)
            }
        }
        isFirstRound = false
        return emptyList()
    }

    private fun processClass(symbol: KSClassDeclaration) {
        Log.debug { metadata ->
            metadata[Node] = symbol
            "Start processing file."
        }
        try {
            val dependencies = symbol.findDependencies()
            Log.verbose { metadata ->
                metadata[Node] = symbol
                "Found dependencies: ${dependencies.originatingFiles.joinToString()}"
            }
            val parameters = symbol.findParameters()
            Log.verbose { metadata ->
                metadata[Node] = symbol
                "Found parameters: ${symbol.findParameters()}"
            }
            val receiver = symbol.asReceiver()
            Log.verbose { metadata ->
                metadata[Node] = symbol
                "Using receiver: $receiver"
            }
            val writer = ExerciseWriter(receiver, parameters)
            val fileSpec = writer.generateFileSpec()
            codeGenerator.createNewFile(dependencies, fileSpec.packageName, fileSpec.name).use { stream ->
                stream.bufferedWriter(Charsets.UTF_8).use { bufferedStream ->
                    fileSpec.writeTo(bufferedStream)
                }
            }
        } catch (t: Throwable) {
            Log.error { metadata ->
                metadata[Node] = symbol
                "An exception was thrown during processing."
            }
        }
        Log.debug { metadata ->
            metadata[Node] = symbol
            "Finished processing file."
        }
    }
}
