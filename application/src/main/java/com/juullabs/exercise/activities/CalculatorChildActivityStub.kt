package com.juullabs.exercise.activities

import com.juullabs.exercise.annotations.AsStub
import com.juullabs.exercise.annotations.Exercise
import com.juullabs.exercise.annotations.Extra
import com.juullabs.exercise.annotations.ResultContract
import com.juullabs.exercise.annotations.ResultKind

@Exercise(Extra("input", Int::class))
@ResultContract(ResultKind("Ok", Extra("output", Int::class)))
@AsStub("com.juullabs.dynamicfeature", "CalculatorChildActivity")
class CalculatorChildActivityStub
