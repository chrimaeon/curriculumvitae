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
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

@LogTag
class SensorDataRepository @Inject constructor(private val sensorManager: SensorManager) {
    val data: Flow<SensorData> = callbackFlow {
        val gravityReading = FloatArray(3)
        val geomagneticReading = FloatArray(3)
        val rotationMatrix = FloatArray(9)
        val orientationAngles = FloatArray(3)

        val callback = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_GRAVITY) {
                    System.arraycopy(event.values, 0, gravityReading, 0, gravityReading.size)
                }

                if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                    System.arraycopy(
                        event.values,
                        0,
                        geomagneticReading,
                        0,
                        geomagneticReading.size
                    )
                }

                if (SensorManager.getRotationMatrix(
                        rotationMatrix,
                        null,
                        gravityReading,
                        geomagneticReading
                    )
                ) {
                    SensorManager.getOrientation(rotationMatrix, orientationAngles)
                    trySend(
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
        }

        sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)?.also { gravity ->
            sensorManager.registerListener(
                callback,
                gravity,
                SensorManager.SENSOR_DELAY_UI
            )
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.also { magneticField ->
            sensorManager.registerListener(
                callback,
                magneticField,
                SensorManager.SENSOR_DELAY_UI
            )
        }

        awaitClose {
            sensorManager.unregisterListener(callback)
        }
    }
}

data class SensorData(
    val roll: Double,
    val pitch: Double
)
