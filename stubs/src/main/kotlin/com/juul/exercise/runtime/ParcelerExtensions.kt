package com.juul.exercise.runtime

import kotlinx.android.parcel.Parceler

@Suppress("UNUSED_PARAMETER") // This is a stub
fun <T : Any> Parceler<T>.createFromMarshalledBytes(
    data: ByteArray,
    offset: Int = 0,
    length: Int = 0
): T = throw NotImplementedError("Stub")

@Suppress("UNUSED_PARAMETER") // This is a stub
fun <T : Any> Parceler<T>.createFromMarshalledBytesOrNull(
    data: ByteArray?,
    offset: Int = 0,
    length: Int = 0
): T? = throw NotImplementedError("Stub")

@Suppress("UNUSED_PARAMETER") // This is a stub
fun <T : Any> Parceler<T>.writeToMarshalledBytes(
    value: T
): ByteArray = throw NotImplementedError("Stub")

@Suppress("UNUSED_PARAMETER") // This is a stub
fun <T : Any> Parceler<T>.writeToMarshalledBytesOrNull(
    value: T?
): ByteArray? = throw NotImplementedError("Stub")
