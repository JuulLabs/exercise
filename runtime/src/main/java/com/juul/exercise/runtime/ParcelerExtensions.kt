package com.juul.exercise.runtime

import android.os.Parcel
import kotlinx.android.parcel.Parceler

fun <T: Any> Parceler<T>.writeToParcel(value: T): Parcel {
    val parcel = Parcel.obtain()
    value.write(parcel, 0)
    parcel.setDataPosition(0)
    return parcel
}

@JvmName("nullableWriteToParcel")
fun <T: Any> Parceler<T>.writeToParcel(value: T?): Parcel? = value?.run(::writeToParcel)
