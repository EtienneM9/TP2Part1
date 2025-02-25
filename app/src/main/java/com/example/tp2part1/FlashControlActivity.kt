package com.example.tp2part1

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import kotlin.math.abs

class FlashControlActivity : ComponentActivity(), SensorEventListener {

    private lateinit var flashStatusImageView: ImageView
    private lateinit var flashStatusTextView: TextView
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private lateinit var cameraManager: CameraManager
    private var cameraId: String? = null
    private var isFlashOn = false
    private var lastShakeTime: Long = 0
    private val SHAKE_THRESHOLD = 500 // Sensibilité du capteur
    private val SHAKE_TIME_THRESHOLD = 80L // latence du contrôle du flash
    private var lastX = 0f
    private var lastY = 0f
    private var lastZ = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.flash_control)

        flashStatusImageView = findViewById(R.id.flashStatusImageView)
        flashStatusTextView = findViewById(R.id.flashStatusTextView)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager

        try {
            cameraId = cameraManager.cameraIdList[0]
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val currentTime = System.currentTimeMillis()
            if ((currentTime - lastShakeTime) > SHAKE_TIME_THRESHOLD) {
                val diffTime = currentTime - lastShakeTime
                lastShakeTime = currentTime

                val speed = abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000

                if (speed > SHAKE_THRESHOLD) {
                    toggleFlash()
                }
                lastX = x
                lastY = y
                lastZ = z
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun toggleFlash() {
        try {
            if (cameraId != null) {
                isFlashOn = !isFlashOn
                cameraManager.setTorchMode(cameraId!!, isFlashOn)
                updateFlashUI()
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun updateFlashUI() {
        if (isFlashOn) {
            flashStatusImageView.setImageResource(R.drawable.flash_on)
            flashStatusTextView.text = "Flash: On"
        } else {
            flashStatusImageView.setImageResource(R.drawable.flash_off)
            flashStatusTextView.text = "Flash: Off"
        }
    }
}
