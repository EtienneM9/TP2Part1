package com.example.tp2part1

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

class DirectionActivity : ComponentActivity(), SensorEventListener {

    private lateinit var directionTextView: TextView
    private lateinit var sensorManager: SensorManager
    private var rotationVectorSensor: Sensor? = null
    private val MOVEMENT_THRESHOLD = 0.1f // Plus sensible pour détecter les rotations

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_direction)

        directionTextView = findViewById(R.id.directionTextView)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        if (rotationVectorSensor == null) {
            directionTextView.text = "Rotation Vector Sensor not available"
        }
    }

    override fun onResume() {
        super.onResume()
        rotationVectorSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            val rotationMatrix = FloatArray(9)
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)

            val orientationValues = FloatArray(3)
            SensorManager.getOrientation(rotationMatrix, orientationValues)

            val azimuth = orientationValues[0] // Rotation autour de l'axe Z
            val pitch = orientationValues[1] // Rotation autour de l'axe X
            val roll = orientationValues[2] // Rotation autour de l'axe Y

            directionTextView.text = when {
                abs(pitch) > MOVEMENT_THRESHOLD -> if (pitch > 0) "Direction: Down" else "Direction: Up"
                abs(roll) > MOVEMENT_THRESHOLD -> if (roll > 0) "Direction: Right" else "Direction: Left"
                else -> "Stable"
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
