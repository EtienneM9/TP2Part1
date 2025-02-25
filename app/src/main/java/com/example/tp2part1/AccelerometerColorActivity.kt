package com.example.tp2part1
import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge

class AccelerometerColorActivity : ComponentActivity(), SensorEventListener {

    private lateinit var accelerometerColorLayout: LinearLayout
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.accelerometre)

        accelerometerColorLayout = findViewById(R.id.accelerometerColorLayout)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (accelerometer == null) {
            accelerometerColorLayout.setBackgroundColor(Color.GRAY)
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
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

            // Calculate the magnitude of the acceleration vector
            val magnitude = Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()

            // Determine the color based on the magnitude
            val color = when {
                magnitude < 10 -> Color.GREEN // Lower values: green
                magnitude > 20 -> Color.RED   // Higher values: red
                else -> Color.BLACK          // Medium values: black
            }

            // Set the background color
            accelerometerColorLayout.setBackgroundColor(color)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed for this example
    }
}