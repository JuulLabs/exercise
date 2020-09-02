package kotlinx.android.parcel

import android.os.Parcel

interface Parceler<T> {
    fun T.write(parcel: Parcel, flags: Int)
    fun create(parcel: Parcel): T
}
