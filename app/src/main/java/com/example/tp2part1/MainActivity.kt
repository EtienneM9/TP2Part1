package com.example.tp2part1


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.Locale
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

class MainActivity : AppCompatActivity(), SensorEventListener, LocationListener,
    OnMapReadyCallback {

    private lateinit var sensorListButton: Button
    private lateinit var sensorDetectionButton: Button
    private lateinit var proximityButton: Button
    private lateinit var directionButton: Button
    private lateinit var locationTextView: TextView
    private lateinit var locationManager: LocationManager
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var isFlashOn = false
    private lateinit var cameraManager: CameraManager
    private var cameraId: String? = null
    private var lastShakeTime: Long = 0
    private val SHAKE_THRESHOLD = 800
    private lateinit var accelerometerColorButton: Button
    private lateinit var flashbtn: Button
    private lateinit var map: GoogleMap
    private var currentLocation: Location? = null
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
        private const val MIN_TIME_BW_UPDATES = 1000L // 1 second
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES = 1f // 1 meter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        sensorListButton = findViewById(R.id.sensorListButton)
        sensorDetectionButton = findViewById(R.id.sensorDetectionButton)
        proximityButton = findViewById(R.id.proximityButton)
        directionButton = findViewById(R.id.directionButton)
        locationTextView = findViewById(R.id.locationTextView)
        accelerometerColorButton = findViewById(R.id.accelerometerColorButton)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        flashbtn = findViewById(R.id.flashControlButton)
        try {
            cameraId = cameraManager.cameraIdList[0]
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

        // Request location permissions
        requestLocationPermissions()

        // Set up button click listeners
        sensorListButton.setOnClickListener {
            startActivity(Intent(this, SensorListActivity::class.java))
        }

        sensorDetectionButton.setOnClickListener {
            startActivity(Intent(this, SensorDetectionActivity::class.java))
        }

        proximityButton.setOnClickListener {
            startActivity(Intent(this, ProximityActivity::class.java))
        }

        directionButton.setOnClickListener {
            startActivity(Intent(this, DirectionActivity::class.java))
        }

        accelerometerColorButton.setOnClickListener {
            startActivity(Intent(this, AccelerometerColorActivity::class.java))
        }

        flashbtn.setOnClickListener {
            startActivity(Intent(this, FlashControlActivity::class.java))
        }


        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        requestLocationPermissions()
        currentLocation?.let {
            updateMapLocation(it)
        }
    }

    private fun requestLocationPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            startLocationUpdates()
        }
    }

    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_BW_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES,
                this
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableUserLocation()
                startLocationUpdates()
            }
        }
    }

    private fun enableUserLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
        }
    }


    override fun onLocationChanged(location: Location) {
        currentLocation = location
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
        if (addresses != null && addresses.isNotEmpty()) {
            val address = addresses[0]
            val locationText = "Location: ${address.locality}, ${address.countryName}"
            locationTextView.text = locationText
        }
        updateMapLocation(location)
    }

    private fun updateMapLocation(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15f)
        map.animateCamera(cameraUpdate)
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

            val currentTime = System.currentTimeMillis()
            if ((currentTime - lastShakeTime) > 100) {
                val diffTime = currentTime - lastShakeTime
                lastShakeTime = currentTime

                val speed = Math.abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000

                if (speed > SHAKE_THRESHOLD) {
                    toggleFlash()
                }

                lastX = x
                lastY = y
                lastZ = z
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed for this example
    }

    private fun toggleFlash() {
        try {
            if (cameraId != null) {
                isFlashOn = !isFlashOn
                cameraManager.setTorchMode(cameraId!!, isFlashOn)
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private var lastX: Float = 0f
    private var lastY: Float = 0f
    private var lastZ: Float = 0f
}
