package com.example.tp2part1

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge

class SensorDetectionActivity : ComponentActivity() {

    private lateinit var sensorStatusTextView: TextView
    private lateinit var sensorManager: SensorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sensor_detection)

        sensorStatusTextView = findViewById(R.id.sensorStatusTextView)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        checkSensorAvailability()
    }

    private fun checkSensorAvailability() {
        val requiredSensors = listOf(
            Sensor.TYPE_ACCELEROMETER,
            Sensor.TYPE_PROXIMITY,
            Sensor.TYPE_GYROSCOPE,
            Sensor.TYPE_LIGHT
        )

        val missingSensors = mutableListOf<String>()

        for (sensorType in requiredSensors) {
            val sensor = sensorManager.getDefaultSensor(sensorType)
            if (sensor == null) {
                missingSensors.add(getSensorName(sensorType))
            }
        }

        if (missingSensors.isEmpty()) {
            sensorStatusTextView.text = "All required sensors are available."
        } else {
            val missingSensorsText = missingSensors.joinToString(", ")
            sensorStatusTextView.text = "The following sensors are missing: $missingSensorsText. Some features may be unavailable."
        }
    }

    private fun getSensorName(sensorType: Int): String {
        return when (sensorType) {
            Sensor.TYPE_ACCELEROMETER -> "Accelerometer"
            Sensor.TYPE_PROXIMITY -> "Proximity Sensor"
            Sensor.TYPE_GYROSCOPE -> "Gyroscope"
            Sensor.TYPE_LIGHT -> "Light Sensor"
            else -> "Unknown Sensor"
        }
    }
}