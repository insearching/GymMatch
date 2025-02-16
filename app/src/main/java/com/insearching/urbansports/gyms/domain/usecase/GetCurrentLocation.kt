package com.insearching.urbansports.gyms.domain.usecase

import com.insearching.urbansports.core.domain.util.DataError
import com.insearching.urbansports.core.domain.util.Result
import com.insearching.urbansports.gyms.domain.LocationManager
import com.insearching.urbansports.gyms.domain.model.GeoPoint

class GetCurrentLocation(
    private val locationManager: LocationManager
) {

    suspend operator fun invoke(): Result<GeoPoint, DataError> {
        return locationManager.getCurrentLocation()
    }
}