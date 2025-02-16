package com.insearching.urbansports.gyms.domain.model

data class GeoPoint(
    val latitude: Double,
    val longitude: Double
) {
    companion object {
        val EMPTY: GeoPoint = GeoPoint(0.0, 0.0)
    }
}