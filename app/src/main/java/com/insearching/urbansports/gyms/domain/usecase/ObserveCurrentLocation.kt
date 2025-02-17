package com.insearching.urbansports.gyms.domain.usecase

import com.insearching.urbansports.core.domain.util.DataError
import com.insearching.urbansports.core.domain.util.Result
import com.insearching.urbansports.core.domain.util.getOrDefault
import com.insearching.urbansports.core.domain.util.onSuccess
import com.insearching.urbansports.gyms.domain.LocationManager
import com.insearching.urbansports.gyms.domain.model.GeoPoint
import com.insearching.urbansports.gyms.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach

/**
 * Observes the current location and emits updates when the location significantly changes.
 *
 * This class utilizes a [LocationManager] to fetch the current location and provides a
 * stream of [GeoPoint] results. It filters out insignificant location changes based on a
 * predefined distance threshold.
 *
 * @property locationManager The [LocationManager] instance used to retrieve location data.
 */
class ObserveCurrentLocation(
    private val locationManager: LocationManager
) {

    private var currentLocation: GeoPoint? = null

    operator fun invoke(): Flow<Result<GeoPoint, DataError>> {
        return locationManager.getCurrentLocation()
            .filter {
                isLocationChanged(
                    oldLocation = currentLocation,
                    newLocation = it.getOrDefault(null)
                )
            }
            .onEach {
                it.onSuccess { location ->
                    currentLocation = location
                }
            }

    }

    private fun isLocationChanged(oldLocation: GeoPoint?, newLocation: GeoPoint?): Boolean {
        if (oldLocation == null) return true
        if (newLocation == null) return false // meaning we have an error while getting the location

        val distance = locationManager.getDistanceBetweenPoints(oldLocation, newLocation)
        return distance > Constants.DISTANCE_THRESHOLD_METERS
    }
}