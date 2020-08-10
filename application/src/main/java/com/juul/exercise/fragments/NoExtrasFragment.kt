package com.juul.exercise.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.juul.exercise.R
import com.juul.exercise.annotations.Exercise
import kotlinx.android.synthetic.main.single_text_view.centeredTextView

@Exercise
class NoExtrasFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.single_text_view, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        centeredTextView.text = "no extras"
    }
}
