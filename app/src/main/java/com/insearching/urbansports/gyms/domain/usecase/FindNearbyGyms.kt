package com.insearching.urbansports.gyms.domain.usecase

import com.insearching.urbansports.core.domain.util.DataError
import com.insearching.urbansports.core.domain.util.Result
import com.insearching.urbansports.core.domain.util.getOrDefault
import com.insearching.urbansports.core.domain.util.map
import com.insearching.urbansports.gyms.domain.GymsRepository
import com.insearching.urbansports.gyms.domain.LocationManager
import com.insearching.urbansports.gyms.domain.model.GeoPoint
import com.insearching.urbansports.gyms.domain.model.Gym
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/**
 * `FindNearbyGyms` is a use case class responsible for finding nearby gyms based on a given current location.
 * It leverages a `GymsRepository` to fetch gyms and then calculates the distance between each gym and the current location.
 * It returns a sorted list of gyms based on their distance from the current location.
 *
 * @property gymsRepository The repository used to fetch gym data. It should implement the [GymsRepository] interface.
 */
class FindNearbyGyms(
    private val gymsRepository: GymsRepository,
    private val locationManager: LocationManager
) {

    operator fun invoke(currentLocation: GeoPoint): Flow<Result<List<Gym>, DataError>> = flow {
        val results = gymsRepository.searchNearbyGyms()
            .map { result ->
                val gyms = result.getOrDefault(emptyList())
                    .map { gym ->
                        val gymLocation = gym.geoPoint ?: return@map gym // gym location should be defined at this point
                        val distance = locationManager.getDistanceBetweenPoints(currentLocation, gymLocation)
                        gym.copy(distance = distance)
                    }
                    .filter { it.distance != null }
                    .sortedBy { it.distance }
                result.map { gyms }
            }
        emitAll(results)
    }
}