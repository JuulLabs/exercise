@file:Suppress("DEPRECATION")

package com.juullabs.exercise.fragments

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.juullabs.exercise.R
import com.juullabs.exercise.annotations.Argument
import com.juullabs.exercise.annotations.Exercise
import kotlinx.android.synthetic.main.single_text_view.centeredTextView

@Exercise(Argument("legacyInt", Int::class))
class LegacyFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.single_text_view, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        centeredTextView.text = "value(legacyInt): ${args.legacyInt}"
    }
}
