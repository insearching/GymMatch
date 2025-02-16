package com.insearching.urbansports.gyms.domain

import com.insearching.urbansports.core.domain.util.DataError
import com.insearching.urbansports.core.domain.util.Result
import com.insearching.urbansports.gyms.domain.model.GeoPoint

interface LocationManager {

    suspend fun getCurrentLocation(): Result<GeoPoint, DataError.Local>

    suspend fun getLatLngFromAddress(address: String): GeoPoint?
}