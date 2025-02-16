package com.insearching.urbansports.gyms.domain.model

import android.location.Location

data class GeoPoint(
    val latitude: Double,
    val longitude: Double
)

fun Location.toGeoPoint(): GeoPoint {
    return GeoPoint(
        latitude = latitude,
        longitude = longitude
    )
}