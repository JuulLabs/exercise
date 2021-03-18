package android.app

import android.content.Intent

abstract class IntentService(name: String) : Service() {
    abstract fun onHandleIntent(intent: Intent?): Unit
}
