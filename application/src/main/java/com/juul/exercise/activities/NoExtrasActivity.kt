package com.juul.exercise.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.juul.exercise.R
import com.juul.exercise.annotations.Exercise
import kotlinx.android.synthetic.main.single_text_view.centeredTextView

@Exercise
class NoExtrasActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.single_text_view)
        centeredTextView.text = "no extras"
    }
}
