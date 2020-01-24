package com.juullabs.exercise.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.juullabs.exercise.R
import com.juullabs.exercise.annotations.Exercise
import com.juullabs.exercise.annotations.Extra
import kotlinx.android.synthetic.main.single_text_view.centeredTextView

@Exercise(Extra("standardInt", Int::class))
class StandardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.single_text_view)
        centeredTextView.text = "value(standardInt): ${extras.standardInt}"
    }
}
