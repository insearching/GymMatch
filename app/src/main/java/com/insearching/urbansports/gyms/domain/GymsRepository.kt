package com.insearching.urbansports.gyms.domain


import com.insearching.urbansports.core.domain.util.DataError
import com.insearching.urbansports.core.domain.util.EmptyResult
import com.insearching.urbansports.core.domain.util.Result
import com.insearching.urbansports.gyms.domain.model.Gym
import kotlinx.coroutines.flow.Flow

interface GymsRepository {
    fun searchNearbyGyms(): Flow<Result<List<Gym>, DataError.Remote>>
    suspend fun updateGym(gym: Gym): EmptyResult<DataError.Local>
}