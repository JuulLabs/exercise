package com.juul.exercise.fragments

import androidx.fragment.app.Fragment
import com.juul.exercise.annotations.Argument
import com.juul.exercise.annotations.Exercise

@Exercise(Argument("superclassString", String::class))
abstract class SuperclassFragment : Fragment()
