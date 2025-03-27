package com.insearching.urbansports.gyms.data.maps

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.os.Looper
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.insearching.urbansports.core.domain.util.DataError
import com.insearching.urbansports.core.domain.util.Result
import com.insearching.urbansports.core.util.LocationUtils
import com.insearching.urbansports.core.util.LocationUtils.hasLocationPermissions
import com.insearching.urbansports.core.util.LocationUtils.isGpsEnabled
import com.insearching.urbansports.gyms.domain.LocationManager
import com.insearching.urbansports.gyms.domain.model.GeoPoint
import com.insearching.urbansports.gyms.domain.model.toGeoPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.timeout
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.time.Duration.Companion.seconds

@OptIn(FlowPreview::class)
@Suppress("DEPRECATION")
class DefaultLocationManager(
    private val context: Context,
    private val locationClient: FusedLocationProviderClient,
    private val geocoder: Geocoder,
) : LocationManager {

    private val LOCATION_TIMEOUT_SECONDS = 10.seconds

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    @SuppressLint("MissingPermission", "ServiceCast")
    override fun getCurrentLocation(): Flow<Result<GeoPoint, DataError.Local>> {
        return callbackFlow {
            val locationRequest = LocationRequest().apply {
                priority = Priority.PRIORITY_HIGH_ACCURACY
                interval = 1000
                fastestInterval = 500
            }

            val locationListener = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    for (location in locationResult.locations) {
                        trySend(Result.Success(location.toGeoPoint()))
                    }
                }

                override fun onLocationAvailability(availability: LocationAvailability) {
                    super.onLocationAvailability(availability)
                    if (availability.isLocationAvailable) {
                        Log.d("LocationAvailability", "Location is available")
                    }
                }
            }
            if (context.hasLocationPermissions() && context.isGpsEnabled()) {
                locationClient.requestLocationUpdates(
                    locationRequest,
                    locationListener,
                    Looper.getMainLooper()
                ).addOnFailureListener {
                    close(it)
                }
            } else {
                trySend(Result.Error(DataError.Local.GPS_DISABLED))
            }
            awaitClose {
                locationClient.removeLocationUpdates(locationListener)
            }
        }
            .timeout(LOCATION_TIMEOUT_SECONDS)
            .catch { exception ->
                if (exception is TimeoutCancellationException) {
                    emit(Result.Error(DataError.Local.NO_LOCATION_FOUND))
                } else {
                    throw exception
                }
            }
            .shareIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(5_000L),
                replay = 1
            )

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