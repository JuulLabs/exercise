package android.app

import android.content.Context
import android.content.Intent

abstract class Activity : Context() {
    val intent: Intent = throw NotImplementedError("Stub")
}
