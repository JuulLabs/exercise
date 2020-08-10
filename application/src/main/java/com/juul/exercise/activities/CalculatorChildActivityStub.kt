package com.juul.exercise.activities

import com.juul.exercise.annotations.AsStub
import com.juul.exercise.annotations.Exercise
import com.juul.exercise.annotations.Extra
import com.juul.exercise.annotations.ResultContract
import com.juul.exercise.annotations.ResultKind

@Exercise(Extra("input", Int::class))
@ResultContract(ResultKind("Ok", Extra("output", Int::class)))
@AsStub("com.juul.dynamicfeature", "CalculatorChildActivity")
class CalculatorChildActivityStub
