package com.parawale.GrocEase.Location.TrackDelivery

import android.location.Location
import com.google.android.gms.maps.model.LatLng

fun calculateDistance(startLatLng: LatLng, endLatLng: LatLng): Float {
    val results = FloatArray(1)
    Location.distanceBetween(
        startLatLng.latitude, startLatLng.longitude,
        endLatLng.latitude, endLatLng.longitude,
        results
    )
    return results[0] // Distance in meters
}

// Function to calculate estimated time (example: 50 meters per second)
fun calculateEstimatedTime(distanceInMeters: Float): String {
    val speedMetersPerSecond = 50 // Assume speed of delivery boy is 50 meters/second
    val timeInSeconds = distanceInMeters / speedMetersPerSecond
    val minutes = (timeInSeconds / 60).toInt()
    return "$minutes minutes"
}