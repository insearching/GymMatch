package com.insearching.urbansports.core.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat

object LocationUtils {

    /**
     * Calculates the great-circle distance between two geographical coordinates using the Haversine formula.
     * This method returns the straight-line distance (as the crow flies) in meters between the starting
     * and ending coordinates.
     *
     * @param startLat The latitude of the starting point in degrees.
     * @param startLng The longitude of the starting point in degrees.
     * @param endLat The latitude of the ending point in degrees.
     * @param endLng The longitude of the ending point in degrees.
     * @return The distance between the two coordinates in meters.
     *
     * Example usage:
     * ```
     * val distance = calculateDistance(48.8566, 2.3522, 51.5074, -0.1278)
     * println("Distance: $distance meters")  // Output: ~343780 meters (343 km)
     * ```
     *
     * Note: This method calculates the shortest distance on a sphere and does not account for roads,
     * traffic, or terrain. Use the Google Distance Matrix API for more accurate travel distances.
     */
    fun calculateDistance(
        startLat: Double,
        startLng: Double,
        endLat: Double,
        endLng: Double
    ): Double {
        val results = FloatArray(1)
        Location.distanceBetween(startLat, startLng, endLat, endLng, results)
        return results[0].toDouble()
    }

    fun Context.hasLocationPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}