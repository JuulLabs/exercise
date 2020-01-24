package com.juullabs.exercise.activities

import androidx.appcompat.app.AppCompatActivity
import com.juullabs.exercise.annotations.Exercise
import com.juullabs.exercise.annotations.Extra

@Exercise(Extra("superclassString", String::class))
abstract class SuperclassActivity : AppCompatActivity()
