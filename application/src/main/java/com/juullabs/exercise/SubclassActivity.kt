package com.juullabs.exercise

import android.os.Bundle
import com.juullabs.exercise.annotations.Exercise
import com.juullabs.exercise.annotations.Extra
import kotlinx.android.synthetic.main.activity_subclass.bottomTextView
import kotlinx.android.synthetic.main.activity_subclass.topTextView

@Exercise(Extra("subclassString", String::class))
class SubclassActivity : SuperclassActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subclass)
        topTextView.text = "value(superclassString): ${extras.superclassString}"
        bottomTextView.text = "value(subclassString): ${extras.subclassString}"
    }
}
