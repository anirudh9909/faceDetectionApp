package com.example.faceDetectionApp

import android.app.Application
import androidx.multidex.MultiDex

class FaceDetectionApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
    }
}
