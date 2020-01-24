package com.juullabs.exercise

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.juullabs.exercise.annotations.Exercise
import com.juullabs.exercise.fragments.newInstanceOfLegacyFragment
import com.juullabs.exercise.fragments.newInstanceOfNoExtrasFragment
import com.juullabs.exercise.fragments.newInstanceOfOptionalFragment
import com.juullabs.exercise.fragments.newInstanceOfStandardFragment
import com.juullabs.exercise.fragments.newInstanceOfSubclassFragment
import kotlinx.android.synthetic.main.activity_fragment_container.showLegacyFragment
import kotlinx.android.synthetic.main.activity_fragment_container.showNoExtrasFragment
import kotlinx.android.synthetic.main.activity_fragment_container.showOptionalFragment
import kotlinx.android.synthetic.main.activity_fragment_container.showStandardFragment
import kotlinx.android.synthetic.main.activity_fragment_container.showSubclassFragment
import kotlin.random.Random

private const val fragmentTag = "fragmentTag"

@Exercise
class FragmentContainerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_container)
        showLegacyFragment.setOnClickListener {
            removeAllFragments()
            addLegacyFragment(newInstanceOfLegacyFragment(15))
        }
        showNoExtrasFragment.setOnClickListener {
            removeAllFragments()
            addFragment(newInstanceOfNoExtrasFragment())
        }
        showOptionalFragment.setOnClickListener {
            removeAllFragments()
            val fragment = if (Random.nextBoolean()) {
                newInstanceOfOptionalFragment("Passed a value")
            } else {
                newInstanceOfOptionalFragment()
            }
            addFragment(fragment)
        }
        showStandardFragment.setOnClickListener {
            removeAllFragments()
            addFragment(newInstanceOfStandardFragment(25))
        }
        showSubclassFragment.setOnClickListener {
            removeAllFragments()
            addFragment(newInstanceOfSubclassFragment("super string", "sub string"))
        }
        showLegacyFragment.performClick()
    }

    private fun removeAllFragments() {
        val legacyFragment = fragmentManager.findFragmentByTag(fragmentTag)
        if (legacyFragment != null) {
            fragmentManager.beginTransaction()
                .remove(legacyFragment)
                .commit()
            fragmentManager.executePendingTransactions()
        }
        val fragment = supportFragmentManager.findFragmentByTag(fragmentTag)
        if (fragment != null) {
            supportFragmentManager.beginTransaction()
                .remove(fragment)
                .commitNow()
        }
    }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, fragment, fragmentTag)
            .commitNow()
    }

    private fun addLegacyFragment(fragment: android.app.Fragment) {
        fragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, fragment, fragmentTag)
            .commit()
        fragmentManager.executePendingTransactions()
    }
}
