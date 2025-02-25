# TP2Part1

## Introduction
This application is designed to help users familiarize themselves with the usage of various sensors available on Android devices. The application demonstrates the use of accelerometer, proximity sensor, and rotation vector sensor. Additionally, it integrates the Google Maps API to display the user's current location.

## Project Setup
To set up the project, follow these steps:

1. Clone the repository:
   ```bash
   git clone <repository-url>
   ```
2. Navigate to the project directory:
   ```bash
   cd TP2Part1
   ```
3. Open the project in Android Studio.
4. Build the project to download the necessary dependencies.

## Sensor Usage

### Accelerometer
The accelerometer is used to detect shake events and change the background color based on the magnitude of the acceleration vector.

**Code Snippet:**
```kotlin
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
```

### Proximity Sensor
The proximity sensor is used to detect the presence of an object near the device and update the UI accordingly.

**Code Snippet:**
```kotlin
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
```

### Rotation Vector Sensor
The rotation vector sensor is used to detect the device's orientation and display the direction (up, down, left, right) based on the pitch and roll values.

**Code Snippet:**
```kotlin
override fun onSensorChanged(event: SensorEvent) {
    if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
        val rotationMatrix = FloatArray(9)
        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)

        val orientationValues = FloatArray(3)
        SensorManager.getOrientation(rotationMatrix, orientationValues)

        val azimuth = orientationValues[0] // Rotation around the Z axis
        val pitch = orientationValues[1] // Rotation around the X axis
        val roll = orientationValues[2] // Rotation around the Y axis

        directionTextView.text = when {
            abs(pitch) > MOVEMENT_THRESHOLD -> if (pitch > 0) "Direction: Down" else "Direction: Up"
            abs(roll) > MOVEMENT_THRESHOLD -> if (roll > 0) "Direction: Right" else "Direction: Left"
            else -> "Stable"
        }
    }
}
```

## Google Maps API
The Google Maps API is integrated to display the user's current location on a map. Location permissions are requested, and location updates are handled to update the map.

**Code Snippet:**
```kotlin
override fun onMapReady(googleMap: GoogleMap) {
    map = googleMap
    requestLocationPermissions()
    currentLocation?.let {
        updateMapLocation(it)
    }
}

private fun updateMapLocation(location: Location) {
    val latLng = LatLng(location.latitude, location.longitude)
    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15f)
    map.animateCamera(cameraUpdate)
}
```

## Frontend Screenshots
The main layout of the application includes a `TextView` for displaying the location, a `SupportMapFragment` for the map, and a series of buttons for navigating to different sensor activities.

**Main Activity Layout:**
```xml
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/mainLayout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="24dp"
    android:background="@color/purple_700"
    tools:context=".MainActivity">

    <TextView
        android:id="@+id/locationTextView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Location: Loading..."
        android:textColor="@color/purple_200"
        android:textSize="20sp"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <fragment
        android:id="@+id/mapView"
        android:name="com.google.android.gms.maps.SupportMapFragment"
        android:layout_width="match_parent"
        android:layout_height="200dp"
        android:layout_marginTop="16dp"
        app:layout_constraintTop_toBottomOf="@id/locationTextView"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

    <LinearLayout
        android:id="@+id/buttonsContainer"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="32dp"
        android:gravity="center"
        android:orientation="vertical"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/mapView">

        <com.google.android.material.button.MaterialButton
            android:id="@+id/sensorListButton"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="16dp"
            android:text="Liste des capteurs"
            android:backgroundTint="@color/purple_200"
            android:textColor="@color/white"
            />

        <com.google.android.material.button.MaterialButton
            android:id="@+id/sensorDetectionButton"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="16dp"
            android:text="Capteurs hors services"
            android:backgroundTint="@color/purple_200"
            android:textColor="@color/button_text_color"
            />

        <com.google.android.material.button.MaterialButton
            android:id="@+id/proximityButton"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="16dp"
            android:text="Capteur de proximité"
            android:backgroundTint="@color/purple_200"
            android:textColor="@color/button_text_color"
            />

        <com.google.android.material.button.MaterialButton
            android:id="@+id/directionButton"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="16dp"
            android:text="Orientation du téléphone"
            android:backgroundTint="@color/purple_200"
            android:textColor="@color/button_text_color"
             />

        <com.google.android.material.button.MaterialButton
            android:id="@+id/accelerometerColorButton"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="16dp"
            android:text="Accélérometre"
            android:backgroundTint="@color/purple_200"
            android:textColor="@color/button_text_color"
            />

        <com.google.android.material.button.MaterialButton
            android:id="@+id/flashControlButton"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="contrôle du flash"
            android:backgroundTint="@color/purple_200"
            android:textColor="@color/button_text_color"
            />

    </LinearLayout>
</androidx.constraintlayout.widget.ConstraintLayout>
