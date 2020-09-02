package android.content

import android.os.Bundle

open class Intent {

    val extras: Bundle = throw NotImplementedError("Stub")

    fun setClassName(packageName: String, className: String): Unit = throw NotImplementedError("Stub")
    fun setClassName(packageContext: Context, className: String): Unit = throw NotImplementedError("Stub")

    fun replaceExtras(extras: Bundle): Unit = throw NotImplementedError("Stub")
}
