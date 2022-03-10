package com.example.treenity_constraint

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import android.util.Log
import android.widget.Toast


class StepDetectorService : Service(), SensorEventListener {

    private var stepsBeforeDetection = 0

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val sensorManager: SensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val countSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) // 기존의 걸음 수도 가져오기에 초기값을 나중에 빼줘야 함

        if(countSensor != null){
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_NORMAL)
        }else{
            Toast.makeText(this, "Sensor Not Detected", Toast.LENGTH_SHORT).show()
        }

        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onSensorChanged(sensor: SensorEvent?) {

        if (stepsBeforeDetection < 1) {
            // initial value
            if (sensor != null) {
                stepsBeforeDetection = sensor.values[0].toInt()
            }
        }
        // 리셋 안된 값 + 현재값 - 리셋 안된 값
        val mSteps = sensor!!.values[0].toInt() - stepsBeforeDetection

        TreenityApplication.steps = mSteps

        Log.d("tag", "onSensorChanged: your step feature is ${TreenityApplication.steps}")
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        Log.d("SERVICE", p0.toString())
    }

}