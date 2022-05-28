/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.android.curriculumvitae.sensor

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.cmgapps.LogTag
import com.cmgapps.android.curriculumvitae.BuildConfig
import kotlinx.coroutines.channels.Channel
import timber.log.Timber
import javax.inject.Inject

@LogTag
class SensorDataRepository @Inject constructor(private val sensorManager: SensorManager?) :
    SensorEventListener {

    fun init() {
        if (BuildConfig.DEBUG) {
            Timber.tag(LOG_TAG).d("SensorDataManager.init()")
        }
        val sensorManager = sensorManager

        if (sensorManager == null) {
            Timber.tag(LOG_TAG).w("SensorManager not available")
            return
        }

        sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)?.also {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_UI
            )
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.also {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_UI
            )
        }
    }

    private val gravityReading = FloatArray(3)
    private val geomagneticReading: FloatArray = FloatArray(3)
    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    val data: Channel<SensorData> = Channel(Channel.CONFLATED)

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_GRAVITY) {
            System.arraycopy(event.values, 0, gravityReading, 0, gravityReading.size)
        }

        if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, geomagneticReading, 0, geomagneticReading.size)
        }

        if (SensorManager.getRotationMatrix(
                rotationMatrix,
                null,
                gravityReading,
                geomagneticReading
            )
        ) {
            SensorManager.getOrientation(rotationMatrix, orientationAngles)
            data.trySend(
                SensorData(
                    roll = orientationAngles[2] * 180 / Math.PI,
                    pitch = orientationAngles[1] * 180 / Math.PI,
                )
            )
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Not needed
    }

    fun cancel() {
        if (BuildConfig.DEBUG) {
            Timber.tag(LOG_TAG).d("SensorDataManager.cancel()")
        }
        sensorManager?.unregisterListener(this)
    }
}

data class SensorData(
    val roll: Double,
    val pitch: Double
)
