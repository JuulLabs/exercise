package androidx.fragment.app

import android.os.Bundle

@Suppress("UNUSED_PARAMETER") // This is a stub
abstract class Fragment {
    var arguments: Bundle?
        get() = throw NotImplementedError("Stub")
        set(value) = throw NotImplementedError("Stub")
}
