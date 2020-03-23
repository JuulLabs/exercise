package com.juullabs.exercise

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.juullabs.exercise.annotations.Exercise
import com.juullabs.exercise.fragments.newLegacyFragment
import com.juullabs.exercise.fragments.newListFragment
import com.juullabs.exercise.fragments.newNoExtrasFragment
import com.juullabs.exercise.fragments.newOptionalFragment
import com.juullabs.exercise.fragments.newStandardFragment
import com.juullabs.exercise.fragments.newSubclassFragment
import kotlinx.android.synthetic.main.activity_fragment_container.showLegacyFragment
import kotlinx.android.synthetic.main.activity_fragment_container.showListFragment
import kotlinx.android.synthetic.main.activity_fragment_container.showNoExtrasFragment
import kotlinx.android.synthetic.main.activity_fragment_container.showOptionalFragment
import kotlinx.android.synthetic.main.activity_fragment_container.showStandardFragment
import kotlinx.android.synthetic.main.activity_fragment_container.showSubclassFragment
import kotlin.random.Random

private const val fragmentTag = "fragmentTag"

@Suppress("DEPRECATION")
@Exercise
class FragmentContainerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_container)
        showLegacyFragment.setOnClickListener {
            removeAllFragments()
            addLegacyFragment(newLegacyFragment(15))
        }
        showListFragment.setOnClickListener {
            removeAllFragments()
            val data = listOf(Datum("First"), Datum("Second"), Datum("Third"))
            addFragment(newListFragment(data))
        }
        showNoExtrasFragment.setOnClickListener {
            removeAllFragments()
            addFragment(newNoExtrasFragment())
        }
        showOptionalFragment.setOnClickListener {
            removeAllFragments()
            val fragment = if (Random.nextBoolean()) {
                newOptionalFragment("Passed a value")
            } else {
                newOptionalFragment()
            }
            addFragment(fragment)
        }
        showStandardFragment.setOnClickListener {
            removeAllFragments()
            addFragment(newStandardFragment(25))
        }
        showSubclassFragment.setOnClickListener {
            removeAllFragments()
            addFragment(newSubclassFragment("super string", "sub string"))
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
