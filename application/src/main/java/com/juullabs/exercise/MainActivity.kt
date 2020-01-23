package com.juullabs.exercise

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.toNoExtrasActivity
import kotlinx.android.synthetic.main.activity_main.toOptionalActivity
import kotlinx.android.synthetic.main.activity_main.toStandardActivity
import kotlinx.android.synthetic.main.activity_main.toSubclassActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toNoExtrasActivity.setOnClickListener {
            startActivity(intentForNoExtrasActivity())
        }
        toOptionalActivity.setOnClickListener {
            // Optionals have a default value of null.
            val intent = if (Random.nextBoolean()) {
                intentForOptionalActivity("Passed a value")
            } else {
                intentForOptionalActivity()
            }
            startActivity(intent)
        }
        toStandardActivity.setOnClickListener {
            startActivity(intentForStandardActivity(50))
        }
        toSubclassActivity.setOnClickListener {
            startActivity(
                intentForSubclassActivity(
                    superclassString = "super",
                    subclassString = "sub"
                )
            )
        }
    }
}
