package com.example.treenity_constraint

import android.app.Application
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import com.example.treenity_constraint.utils.MyWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class TreenityApplication : Application() {


    companion object{
        var steps = 0
    }
}