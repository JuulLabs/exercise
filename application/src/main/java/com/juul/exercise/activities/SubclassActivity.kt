package com.juul.exercise.activities

import android.os.Bundle
import com.juul.exercise.R
import com.juul.exercise.annotations.Exercise
import com.juul.exercise.annotations.Extra
import kotlinx.android.synthetic.main.subclass.bottomTextView
import kotlinx.android.synthetic.main.subclass.topTextView

@Exercise(Extra("subclassString", String::class))
class SubclassActivity : SuperclassActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.subclass)
        topTextView.text = "value(superclassString): ${extras.superclassString}"
        bottomTextView.text = "value(subclassString): ${extras.subclassString}"
    }
}
