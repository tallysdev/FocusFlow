package com.example.focusflow // Or your actual package name

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp // Crucial for Hilt: This triggers Hilt's code generation.
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Application-level initializations can go here.
        // For example:
        // - Setting up logging libraries (Timber)
        // - Initializing analytics
        // - Any other setup that needs to happen once when the app starts.
        // For a simple app, this onCreate() might even be empty if Hilt is all you need it for.
    }
}