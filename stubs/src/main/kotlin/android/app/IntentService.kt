package android.app

import android.content.Intent

@Suppress("UNUSED_PARAMETER") // This is a stub
abstract class IntentService(name: String) : Service() {
    abstract fun onHandleIntent(intent: Intent?): Unit
}
