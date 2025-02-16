package com.insearching.urbansports.gyms.data.maps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.insearching.urbansports.core.domain.util.DataError
import com.insearching.urbansports.core.domain.util.Result
import com.insearching.urbansports.gyms.domain.LocationManager
import com.insearching.urbansports.gyms.domain.model.GeoPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

@Suppress("DEPRECATION")
class DefaultLocationManager(
    private val context: Context,
    private val locationClient: FusedLocationProviderClient,
    private val geocoder: Geocoder,
) : LocationManager {

    override suspend fun getCurrentLocation(): Result<GeoPoint, DataError.Local> {
        return suspendCancellableCoroutine { continuation ->
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                locationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        continuation.resume(Result.Success(location.toGeoPoint()))
                    } else {
                        continuation.resume(Result.Error(DataError.Local.GPS_DISABLED))
                    }
                }
            } else {
                continuation.resume(Result.Error(DataError.Local.PERMISSION_REQUIRED))
            }
        }
    }

    override suspend fun getLatLngFromAddress(address: String): GeoPoint? {
        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { continuation ->
                val addresses = geocoder.getFromLocationName(address, 1)
                if (addresses?.isNotEmpty() == true) {
                    val location = addresses[0]
                    continuation.resume(GeoPoint(location.latitude, location.longitude))
                } else {
                    continuation.resume(null)
                }
            }
        }
    }
}

fun Location.toGeoPoint(): GeoPoint {
    return GeoPoint(
        latitude = latitude,
        longitude = longitude
    )
}