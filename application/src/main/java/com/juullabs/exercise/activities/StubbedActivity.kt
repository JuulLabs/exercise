package com.juullabs.exercise.activities

import androidx.appcompat.app.AppCompatActivity
import com.juullabs.exercise.annotations.AsStub
import com.juullabs.exercise.annotations.Exercise
import com.juullabs.exercise.annotations.Extra
import com.juullabs.exercise.annotations.FromStub

@Exercise(Extra("example", String::class))
@AsStub("com.juullabs.exercise.activities", "StubbedActivity")
private class StubbedActivityStubs

@Exercise
@FromStub(StubbedActivityStubs::class)
class StubbedActivity : AppCompatActivity() {
}
