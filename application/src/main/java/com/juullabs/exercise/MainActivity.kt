package com.juullabs.exercise

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.juullabs.exercise.activities.CalculatorParentActivityIntent
import com.juullabs.exercise.activities.ListActivityIntent
import com.juullabs.exercise.activities.NoExtrasActivityIntent
import com.juullabs.exercise.activities.OptionalActivityIntent
import com.juullabs.exercise.activities.StandardActivityIntent
import com.juullabs.exercise.activities.SubclassActivityIntent
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
            startActivity(FragmentContainerActivityIntent())
        }
        toCalculatorParentActivity.setOnClickListener {
            startActivity(CalculatorParentActivityIntent())
        }
        toListActivity.setOnClickListener {
            val data = listOf(Datum("First"), Datum("Second"), Datum("Third"))
            startActivity(ListActivityIntent(data))
        }
        toNoExtrasActivity.setOnClickListener {
            startActivity(NoExtrasActivityIntent())
        }
        toOptionalActivity.setOnClickListener {
            // Optionals have a default value of null.
            val intent = if (Random.nextBoolean()) {
                OptionalActivityIntent("Passed a value")
            } else {
                OptionalActivityIntent()
            }
            startActivity(intent)
        }
        toStandardActivity.setOnClickListener {
            startActivity(StandardActivityIntent(50))
        }
        toSubclassActivity.setOnClickListener {
            startActivity(
                SubclassActivityIntent(
                    superclassString = "super",
                    subclassString = "sub"
                )
            )
        }
    }
}
