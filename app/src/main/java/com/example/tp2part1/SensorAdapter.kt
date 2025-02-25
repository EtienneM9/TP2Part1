package com.example.tp2part1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tp2part1.R

class SensorAdapter(private val sensorList: List<SensorItem>) :
    RecyclerView.Adapter<SensorAdapter.SensorViewHolder>() {

    class SensorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sensorName: TextView = itemView.findViewById(R.id.sensorName)
        val sensorType: TextView = itemView.findViewById(R.id.sensorType)
        val sensorVendor: TextView = itemView.findViewById(R.id.sensorVendor)
        val sensorVersion: TextView = itemView.findViewById(R.id.sensorVersion)
        val sensorResolution: TextView = itemView.findViewById(R.id.sensorResolution)
        val sensorPower: TextView = itemView.findViewById(R.id.sensorPower)
        val sensorMaximumRange: TextView = itemView.findViewById(R.id.sensorMaximumRange)
        val sensorMinDelay: TextView = itemView.findViewById(R.id.sensorMinDelay)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SensorViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.sensor_item, parent, false)
        return SensorViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SensorViewHolder, position: Int) {
        val currentSensor = sensorList[position]
        holder.sensorName.text = "Name: ${currentSensor.name}"
        holder.sensorType.text = "Type: ${currentSensor.type}"
        holder.sensorVendor.text = "Vendor: ${currentSensor.vendor}"
        holder.sensorVersion.text = "Version: ${currentSensor.version}"
        holder.sensorResolution.text = "Resolution: ${currentSensor.resolution}"
        holder.sensorPower.text = "Power: ${currentSensor.power}"
        holder.sensorMaximumRange.text = "Maximum Range: ${currentSensor.maximumRange}"
        holder.sensorMinDelay.text = "Minimum Delay: ${currentSensor.minDelay}"
    }

    override fun getItemCount(): Int = sensorList.size
}