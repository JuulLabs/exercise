package com.juul.exercise.runtime

import android.os.Parcel
import kotlinx.parcelize.Parceler

fun <T : Any> Parceler<T>.createFromMarshalledBytes(
    data: ByteArray,
    offset: Int = 0,
    length: Int = data.size - offset,
): T {
    val parcel = Parcel.obtain()
    parcel.unmarshall(data, offset, length)
    parcel.setDataPosition(0)
    val value = this.create(parcel)
    parcel.recycle()
    return value
}

fun <T : Any> Parceler<T>.createFromMarshalledBytesOrNull(
    data: ByteArray?,
    offset: Int = 0,
    length: Int = (data ?: ByteArray(0)).size - offset,
): T? = data?.run { createFromMarshalledBytes(data, offset, length) }

fun <T : Any> Parceler<T>.writeToMarshalledBytes(
    value: T,
): ByteArray {
    val parcel = Parcel.obtain()
    value.write(parcel, 0)
    val byteArray = parcel.marshall()
    parcel.recycle()
    return byteArray
}

fun <T : Any> Parceler<T>.writeToMarshalledBytesOrNull(
    value: T?,
): ByteArray? = value?.run(::writeToMarshalledBytes)
