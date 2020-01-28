package com.juullabs.exercise.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.juullabs.exercise.Datum
import com.juullabs.exercise.R
import com.juullabs.exercise.annotations.Argument
import com.juullabs.exercise.annotations.Exercise
import kotlinx.android.synthetic.main.single_text_view.centeredTextView

@Exercise(Argument("data", List::class, Datum::class))
class ListFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.single_text_view, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        centeredTextView.text = "value(data): ${args.data.joinToString(separator = "\n")}"
    }
}
