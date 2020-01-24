package com.juullabs.exercise.fragments

import androidx.fragment.app.Fragment
import com.juullabs.exercise.annotations.Argument
import com.juullabs.exercise.annotations.Exercise

@Exercise(Argument("superclassString", String::class))
abstract class SuperclassFragment : Fragment()
