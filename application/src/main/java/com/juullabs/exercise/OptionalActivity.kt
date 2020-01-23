package com.juullabs.exercise

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.juullabs.exercise.annotations.Exercise
import com.juullabs.exercise.annotations.Extra
import kotlinx.android.synthetic.main.activity_single_text_view.centeredTextView

@Exercise(Extra("optionalString", String::class, optional = true))
class OptionalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_text_view)
        centeredTextView.text = "value(optionalString): ${extras.optionalString}"
    }
}
