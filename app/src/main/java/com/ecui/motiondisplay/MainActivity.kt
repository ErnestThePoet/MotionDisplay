package com.ecui.motiondisplay

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.util.*
import kotlin.math.abs
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var tvSpeed: TextView
    private lateinit var tvAcceleration: TextView
    private lateinit var tvSideAcc: TextView
    private lateinit var tvSideAccLArrow: TextView
    private lateinit var tvSideAccRArrow: TextView
    private lateinit var tvGLoad: TextView
    private lateinit var tvTotalAcc: TextView

    private lateinit var sensorManager: SensorManager
    private lateinit var locationHelper: LocationHelper

    private lateinit var accelerometerReading: FloatArray

    private lateinit var accSwFilters: Array<SWFilter>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvSpeed = findViewById(R.id.tvSpeed)
        tvAcceleration = findViewById(R.id.tvAcceleration)
        tvSideAcc = findViewById(R.id.tvSideAcc)
        tvSideAccLArrow = findViewById(R.id.tvSideAccLArrow)
        tvSideAccRArrow = findViewById(R.id.tvSideAccRArrow)
        tvGLoad = findViewById(R.id.tvGLoad)
        tvTotalAcc = findViewById(R.id.tvTotalAcc)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        locationHelper = LocationHelper(this)

        accelerometerReading = FloatArray(3)
        accSwFilters = Array(3) { SWFilter(16) }

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            ActivityCompat.requestPermissions(this, permissions, 123)
        }

        Timer().schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    updateDisplay()
                }
            }
        }, 0, 67)
    }

    fun updateDisplay() {
        val location = locationHelper.getLocation()
        if (location != null) {
            tvSpeed.text = String.format("%.1f", location.speed * 3.6)
        }

        tvAcceleration.text = String.format("%.2f", accelerometerReading[1])

        tvSideAcc.text = String.format("%.2f", abs(accelerometerReading[0]))
        tvSideAccLArrow.visibility =
            if (accelerometerReading[0] < 0) View.VISIBLE else View.INVISIBLE
        tvSideAccRArrow.visibility =
            if (accelerometerReading[0] > 0) View.VISIBLE else View.INVISIBLE

        val gravity: Float = 9.80665f

        tvGLoad.text = String.format("%+.2f", accelerometerReading[2] / gravity)

        tvTotalAcc.text = String.format(
            "%.2f",
            abs(
                sqrt(
                    accelerometerReading[0] * accelerometerReading[0]
                            + accelerometerReading[1] * accelerometerReading[1]
                            + accelerometerReading[2] * accelerometerReading[2]
                ) - gravity
            )
        )
    }

    override fun onResume() {
        super.onResume()
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (accelerometer != null) {
            sensorManager.registerListener(
                this, accelerometer, SensorManager.SENSOR_DELAY_GAME,
                SensorManager.SENSOR_DELAY_GAME
            )
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) {
            return
        }

        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            for (i in accelerometerReading.indices) {
                accelerometerReading[i] = accSwFilters[i].filter(event.values[i])
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}
}