package com.juullabs.exercise

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/** The details of this class doesn't matter. Just serves as a sample parcelable. */
@Parcelize
data class Datum(val value: String): Parcelable
