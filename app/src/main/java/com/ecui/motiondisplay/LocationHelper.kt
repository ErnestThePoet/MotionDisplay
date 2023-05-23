package com.ecui.motiondisplay

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.widget.Toast
import androidx.core.app.ActivityCompat

class LocationHelper(context: Context) : LocationListener {
    private val context = context
    private val locationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    public fun getLocation(): Location? {
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (!isGpsEnabled) {
            Toast.makeText(context, "GPS not enabled", Toast.LENGTH_SHORT).show()
            return null
        } else {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(context, "No GPS permission", Toast.LENGTH_SHORT).show()
                return null
            }

            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 100, 1f, this)
            return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        }
    }

    override fun onLocationChanged(p0: Location) {}
}