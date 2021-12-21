package com.juul.exercise.runtime

import android.os.Parcel
import kotlinx.parcelize.Parceler
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.Config.NONE

data class Sample(val value: String)

object SampleParceler : Parceler<Sample> {
    override fun create(parcel: Parcel): Sample = Sample(checkNotNull(parcel.readString()))

    override fun Sample.write(parcel: Parcel, flags: Int) {
        parcel.writeString(value)
    }
}

@RunWith(RobolectricTestRunner::class)
@Config(manifest = NONE)
class ParcelerExtensionTests {

    @Test
    fun `round trip through extension functions returns same object`() {
        val expected = Sample("expected")
        val data = SampleParceler.writeToMarshalledBytes(expected)
        val actual = SampleParceler.createFromMarshalledBytes(data)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `null input marshals to null byte array`() {
        val expected: Sample? = null
        val data = SampleParceler.writeToMarshalledBytesOrNull(expected)
        assertThat(data).isNull()
    }
}
