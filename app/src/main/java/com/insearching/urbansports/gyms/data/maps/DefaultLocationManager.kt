package com.insearching.urbansports.gyms.data.maps

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.insearching.urbansports.core.domain.util.DataError
import com.insearching.urbansports.core.domain.util.Result
import com.insearching.urbansports.core.util.LocationUtils
import com.insearching.urbansports.core.util.LocationUtils.hasLocationPermissions
import com.insearching.urbansports.gyms.domain.LocationManager
import com.insearching.urbansports.gyms.domain.model.GeoPoint
import com.insearching.urbansports.gyms.domain.model.toGeoPoint
import com.insearching.urbansports.gyms.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.time.Duration.Companion.seconds

@Suppress("DEPRECATION")
class DefaultLocationManager(
    private val context: Context,
    private val locationClient: FusedLocationProviderClient,
    private val geocoder: Geocoder,
) : LocationManager {

    private val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        Constants.LOCATION_UPDATE_SECONDS.seconds.inWholeMilliseconds
    ).build()

    @SuppressLint("MissingPermission")
    override fun getCurrentLocation(): Flow<Result<GeoPoint, DataError.Local>> {
        return callbackFlow {
            val locationListener = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val lastLocation = locationResult.lastLocation
                    if (lastLocation != null) {
                        trySend(Result.Success(lastLocation.toGeoPoint()))
                    } else {
                        trySend(Result.Error(DataError.Local.GPS_DISABLED))
                    }
                }
            }

            if (context.hasLocationPermissions()) {
                locationClient.requestLocationUpdates(
                    locationRequest,
                    locationListener,
                    Looper.getMainLooper()
                )
            }
            awaitClose {
                locationClient.removeLocationUpdates(locationListener) // Unregister listener on cancellation
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

    // TODO: Refactor logic to get more precise distance based on maps
    override fun getDistanceBetweenPoints(startLocation: GeoPoint, endLocation: GeoPoint): Double {
        return LocationUtils.calculateDistance(
            startLat = startLocation.latitude,
            startLng = startLocation.longitude,
            endLat = endLocation.latitude,
            endLng = endLocation.longitude
        )
    }
}