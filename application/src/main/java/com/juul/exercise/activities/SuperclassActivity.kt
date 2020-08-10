package com.juul.exercise.activities

import androidx.appcompat.app.AppCompatActivity
import com.juul.exercise.annotations.Exercise
import com.juul.exercise.annotations.Extra

@Exercise(Extra("superclassString", String::class))
abstract class SuperclassActivity : AppCompatActivity()
