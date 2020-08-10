package com.juul.dynamicfeature

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.juul.exercise.activities.CalculatorChildActivityStub
import com.juul.exercise.annotations.Exercise
import com.juul.exercise.annotations.FromStub
import kotlinx.android.synthetic.main.activity_calc_child.cancel
import kotlinx.android.synthetic.main.activity_calc_child.inputTextView
import kotlinx.android.synthetic.main.activity_calc_child.plus1
import kotlinx.android.synthetic.main.activity_calc_child.times3

@Exercise
@FromStub(CalculatorChildActivityStub::class)
class CalculatorChildActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calc_child)
        inputTextView.text = extras.input.toString()
        plus1.setOnClickListener { finishWithOk(extras.input + 1) }
        times3.setOnClickListener { finishWithOk(extras.input * 3) }
        cancel.setOnClickListener { finishWithCanceled() }
    }
}
