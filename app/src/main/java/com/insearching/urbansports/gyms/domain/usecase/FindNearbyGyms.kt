package com.insearching.urbansports.gyms.domain.usecase

import com.insearching.urbansports.core.domain.util.DataError
import com.insearching.urbansports.core.domain.util.Result
import com.insearching.urbansports.core.domain.util.getOrDefault
import com.insearching.urbansports.core.domain.util.map
import com.insearching.urbansports.core.util.LocationUtils
import com.insearching.urbansports.gyms.domain.GymsRepository
import com.insearching.urbansports.gyms.domain.model.GeoPoint
import com.insearching.urbansports.gyms.domain.model.Gym
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class FindNearbyGyms(
    private val gymsRepository: GymsRepository
) {

    operator fun invoke(currentLocation: GeoPoint): Flow<Result<List<Gym>, DataError>> = flow {
        val results = gymsRepository.searchNearbyGyms()
            .map { result ->
                val gyms = result.getOrDefault(emptyList())
                    .map { gym ->
                        val geoPoint = gym.geoPoint ?: return@map gym // gym location should be defined at this point
                        val distance = LocationUtils.calculateDistance(
                            startLat = currentLocation.latitude,
                            startLng = currentLocation.longitude,
                            endLat = geoPoint.latitude,
                            endLng = geoPoint.longitude
                        ).toDouble()
                        gym.copy(distance = distance)
                    }
                    .filter { it.distance != null }
                    .sortedBy { it.distance }
                result.map { gyms }
            }
        emitAll(results)
    }
}