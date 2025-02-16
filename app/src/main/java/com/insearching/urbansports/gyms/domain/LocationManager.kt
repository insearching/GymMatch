package com.insearching.urbansports.gyms.domain

import com.insearching.urbansports.core.domain.util.DataError
import com.insearching.urbansports.core.domain.util.Result
import com.insearching.urbansports.gyms.domain.model.GeoPoint
import kotlinx.coroutines.flow.Flow

interface LocationManager {

    suspend fun getCurrentLocation(): Flow<Result<GeoPoint, DataError.Local>>

    suspend fun getLatLngFromAddress(address: String): GeoPoint?

    fun getDistanceBetweenPoints(startLocation: GeoPoint, endLocation: GeoPoint): Double
}