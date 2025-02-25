package com.example.tp2part1

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge

class ProximityActivity : ComponentActivity(), SensorEventListener {

    private lateinit var proximityImageView: ImageView
    private lateinit var proximityTextView: TextView
    private lateinit var sensorManager: SensorManager
    private var proximitySensor: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.proximity_layout)

        proximityImageView = findViewById(R.id.proximityImageView)
        proximityTextView = findViewById(R.id.proximityTextView)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

        if (proximitySensor == null) {
            proximityTextView.text = "Proximity sensor not available"
        }
    }

    override fun onResume() {
        super.onResume()
        proximitySensor?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_PROXIMITY) {
            val distance = event.values[0]
            if (distance < proximitySensor!!.maximumRange) {
                // Object is close
                proximityImageView.setImageResource(R.drawable.close)
                proximityTextView.text = "Object is close"
            } else {
                // Object is far
                proximityImageView.setImageResource(R.drawable.far)
                proximityTextView.text = "Object is far"
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed for this example
    }
}