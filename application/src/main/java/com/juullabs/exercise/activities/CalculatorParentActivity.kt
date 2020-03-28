package com.juullabs.exercise.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.juullabs.dynamicfeature.CalculatorChildActivityContract
import com.juullabs.dynamicfeature.CalculatorChildActivityIntent
import com.juullabs.dynamicfeature.CalculatorChildActivityResult
import com.juullabs.exercise.R
import com.juullabs.exercise.annotations.Exercise
import kotlinx.android.synthetic.main.activity_calc_parent.initialValue11
import kotlinx.android.synthetic.main.activity_calc_parent.initialValue3
import kotlinx.android.synthetic.main.activity_calc_parent.initialValue5
import kotlinx.android.synthetic.main.activity_calc_parent.initialValue7
import kotlinx.android.synthetic.main.activity_calc_parent.resultTextView

@Exercise
class CalculatorParentActivity : AppCompatActivity() {

    private val calculator = prepareCall(CalculatorChildActivityContract(this)) { result ->
        resultTextView.text = when (result) {
            null -> "Implicitly canceled"
            is CalculatorChildActivityResult.Canceled -> "Explicitly canceled"
            is CalculatorChildActivityResult.Ok -> "Result: ${result.output}"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calc_parent)
        initialValue3.setOnClickListener { calculator.launch(CalculatorChildActivityIntent(this, 3)) }
        initialValue5.setOnClickListener { calculator.launch(CalculatorChildActivityIntent(this, 5)) }
        initialValue7.setOnClickListener { calculator.launch(CalculatorChildActivityIntent(this, 7)) }
        initialValue11.setOnClickListener { calculator.launch(CalculatorChildActivityIntent(this, 11)) }
    }
}
