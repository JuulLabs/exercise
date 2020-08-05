package com.juul.exercise.runtime

import android.os.Parcel
import kotlinx.android.parcel.Parceler
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

data class Sample(val value: String)

object SampleParceler : Parceler<Sample> {
    override fun create(parcel: Parcel): Sample = Sample(checkNotNull(parcel.readString()))

    override fun Sample.write(parcel: Parcel, flags: Int) {
        parcel.writeString(value)
    }

}

@RunWith(RobolectricTestRunner::class)
class ParcelerExtensionTests {

    @Test
    fun `round trip parcel creation returns same object`() {
        val expected = Sample("expected")
        val parcel = SampleParceler.writeToParcel(expected)
        val actual = SampleParceler.create(parcel)
        parcel.recycle()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `null input creates a null parcel`() {
        val expected: Sample? = null
        val parcel = SampleParceler.writeToParcel(expected)
        try {
            assertThat(parcel).isNull()
        } finally {
            // If the test fails we actually have cleanup to do.
            parcel?.recycle()
        }
    }
}
