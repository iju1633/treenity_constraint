package com.example.treenity_constraint

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TreenityApplication : Application() {

    companion object{
        var steps = 0
    }
}