package com.juul.exercise

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.juul.exercise.activities.CalculatorParentActivityIntent
import com.juul.exercise.activities.ListActivityIntent
import com.juul.exercise.activities.NoExtrasActivityIntent
import com.juul.exercise.activities.OptionalActivityIntent
import com.juul.exercise.activities.StandardActivityIntent
import com.juul.exercise.activities.SubclassActivityIntent
import kotlinx.android.synthetic.main.activity_main.toCalculatorParentActivity
import kotlinx.android.synthetic.main.activity_main.toFragmentContainer
import kotlinx.android.synthetic.main.activity_main.toListActivity
import kotlinx.android.synthetic.main.activity_main.toNoExtrasActivity
import kotlinx.android.synthetic.main.activity_main.toOptionalActivity
import kotlinx.android.synthetic.main.activity_main.toStandardActivity
import kotlinx.android.synthetic.main.activity_main.toSubclassActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toFragmentContainer.setOnClickListener {
            startActivity(FragmentContainerActivityIntent(this))
        }
        toCalculatorParentActivity.setOnClickListener {
            startActivity(CalculatorParentActivityIntent(this))
        }
        toListActivity.setOnClickListener {
            val data = listOf(Datum("First"), Datum("Second"), Datum("Third"))
            startActivity(ListActivityIntent(this, data))
        }
        toNoExtrasActivity.setOnClickListener {
            startActivity(NoExtrasActivityIntent(this))
        }
        toOptionalActivity.setOnClickListener {
            // Optionals have a default value of null.
            val intent = if (Random.nextBoolean()) {
                OptionalActivityIntent(this, "Passed a value")
            } else {
                OptionalActivityIntent(this)
            }
            startActivity(intent)
        }
        toStandardActivity.setOnClickListener {
            startActivity(StandardActivityIntent(this, 50))
        }
        toSubclassActivity.setOnClickListener {
            startActivity(
                SubclassActivityIntent(
                    this,
                    superclassString = "super",
                    subclassString = "sub"
                )
            )
        }
    }
}
