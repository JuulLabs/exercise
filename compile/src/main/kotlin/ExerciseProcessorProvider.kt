package com.juul.exercise.compile

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.juul.khronicle.Log

public class ExerciseProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        Log.dispatcher.install(KspKhronicleLogger(environment.logger))
        return ExerciseProcessor(environment.codeGenerator)
    }
}
