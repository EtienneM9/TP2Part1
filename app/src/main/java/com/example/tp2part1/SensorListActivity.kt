package com.example.tp2part1

import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SensorListActivity : ComponentActivity() {

    private lateinit var Slist: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.sensor_list)

        Slist = findViewById(R.id.AvailableSensorList)
        Slist.layoutManager = LinearLayoutManager(this)

        // Instancier le SensorManager
        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        // Obtenir la liste de tous les capteurs
        val sensors = sensorManager.getSensorList(Sensor.TYPE_ALL)

        // Convertir la liste de Sensor en liste de SensorItem
        val sensorItems = sensors.map { SensorItem(it) }

        // Créer l'adaptateur
        val adapter = SensorAdapter(sensorItems)

        // Définir l'adaptateur sur la RecyclerView
        Slist.adapter = adapter
    }
}