package com.juul.exercise.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.juul.exercise.Datum
import com.juul.exercise.R
import com.juul.exercise.annotations.Exercise
import com.juul.exercise.annotations.Extra
import kotlinx.android.synthetic.main.single_text_view.centeredTextView

@Exercise(Extra("data", List::class, Datum::class))
class ListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.single_text_view)
        centeredTextView.text = "value(data): ${extras.data.joinToString(separator = "\n")}"
    }
}
