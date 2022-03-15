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

        private val constraints = androidx.work.Constraints.Builder()
            .setRequiresCharging(false)
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresCharging(false)
            .setRequiresBatteryNotLow(true)
            .build()

        val myRequest = PeriodicWorkRequest.Builder(
            MyWorker::class.java,
            15, // 최소 시간이 15분 -> TODO 나중에 1시간으로 설정할 것
            TimeUnit.MINUTES
        ).setConstraints(constraints)
    }
}