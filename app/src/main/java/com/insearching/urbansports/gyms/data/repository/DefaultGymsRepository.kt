package com.insearching.urbansports.gyms.data.repository

import android.database.sqlite.SQLiteException
import com.insearching.urbansports.core.domain.util.DataError
import com.insearching.urbansports.core.domain.util.EmptyResult
import com.insearching.urbansports.core.domain.util.Result
import com.insearching.urbansports.core.domain.util.map
import com.insearching.urbansports.core.domain.util.onSuccess
import com.insearching.urbansports.gyms.data.database.GymDao
import com.insearching.urbansports.gyms.data.database.toEntity
import com.insearching.urbansports.gyms.data.database.toGym
import com.insearching.urbansports.gyms.data.mapper.toGym
import com.insearching.urbansports.gyms.data.networking.RemoteGymDataSource
import com.insearching.urbansports.gyms.domain.GymsRepository
import com.insearching.urbansports.gyms.domain.LocationManager
import com.insearching.urbansports.gyms.domain.model.Gym
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class DefaultGymsRepository(
    private val remoteGymDataSource: RemoteGymDataSource,
    private val gymDao: GymDao,
    private val locationManager: LocationManager
) : GymsRepository {

    override fun searchNearbyGyms(): Flow<Result<List<Gym>, DataError.Remote>> = flow {
        val cachedGyms = getCachedGyms()
        if (cachedGyms.isNotEmpty()) {
            emit(Result.Success(cachedGyms.map { it.toGym() }))
        }

        val remoteGymsResult = remoteGymDataSource.searchOpenGyms()
            .map { response ->
                response.gyms.map {
                    val gym = it.toGym()
                    val geoPoint = locationManager.getLatLngFromAddress(gym.getSearchAddress())
                    gym.copy(geoPoint = geoPoint)
                }
            }.onSuccess { gyms ->
                gymDao.addGyms(gyms.map { it.toEntity() })
            }
        emit(remoteGymsResult)
    }

    override suspend fun updateGym(gym: Gym): EmptyResult<DataError.Local> {
        return try {
            gymDao.updateGym(gym.toEntity())
            Result.Success(Unit)
        } catch (ex: SQLiteException) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    private suspend fun getCachedGyms() =
        withContext(Dispatchers.IO) { gymDao.getAllGyms() }

}