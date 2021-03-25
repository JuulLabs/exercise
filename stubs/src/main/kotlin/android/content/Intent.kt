package android.content

import android.os.Bundle

@Suppress("UNUSED_PARAMETER") // This is a stub
open class Intent {

    val extras: Bundle = throw NotImplementedError("Stub")

    fun setClassName(packageName: String, className: String): Unit = throw NotImplementedError("Stub")
    fun setClassName(packageContext: Context, className: String): Unit = throw NotImplementedError("Stub")

    fun replaceExtras(extras: Bundle): Unit = throw NotImplementedError("Stub")
}
