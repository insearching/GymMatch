package com.insearching.urbansports.gyms.data.database

import androidx.room.TypeConverter
import com.insearching.urbansports.gyms.domain.model.GeoPoint

object GeoPointStringConverter {

    @TypeConverter
    fun fromString(value: String): GeoPoint {
        val pointParts = value.split(":")
        return GeoPoint(
            latitude = pointParts[0].toDouble(),
            longitude = pointParts[1].toDouble()
        )
    }

    @TypeConverter
    fun fromGeoPoint(geoPoint: GeoPoint): String {
        return "${geoPoint.latitude}:${geoPoint.longitude}"
    }
}