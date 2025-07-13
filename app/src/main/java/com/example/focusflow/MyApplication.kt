package com.example.focusflow

import android.app.Application
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.initialize
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp // Crucial for Hilt: This triggers Hilt's code generation.
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Firebase.initialize(this)

        val settings =
            FirebaseFirestoreSettings.Builder()
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build()

        FirebaseFirestore.getInstance().firestoreSettings = settings

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("GlobalError", "Uncaught exception: ${throwable.message}", throwable)
        }
        // Application-level initializations can go here.
        // For example:
        // - Setting up logging libraries (Timber)
        // - Initializing analytics
        // - Any other setup that needs to happen once when the app starts.
        // For a simple app, this onCreate() might even be empty if Hilt is all you need it for.
    }
}
