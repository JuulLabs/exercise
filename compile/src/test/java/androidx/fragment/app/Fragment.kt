package androidx.fragment.app

import android.os.Bundle

abstract class Fragment {
    var arguments: Bundle?
        get() = throw NotImplementedError("Stub")
        set(value) = throw NotImplementedError("Stub")
}
