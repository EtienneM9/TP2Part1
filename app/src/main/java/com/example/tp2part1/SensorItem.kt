package com.example.tp2part1

import android.hardware.Sensor

data class SensorItem(
    val name: String,
    val type: Int,
    val vendor: String,
    val version: Int,
    val resolution: Float,
    val power: Float,
    val maximumRange: Float,
    val minDelay: Int
) {
    constructor(sensor: Sensor) : this(
        sensor.name,
        sensor.type,
        sensor.vendor,
        sensor.version,
        sensor.resolution,
        sensor.power,
        sensor.maximumRange,
        sensor.minDelay
    )
}