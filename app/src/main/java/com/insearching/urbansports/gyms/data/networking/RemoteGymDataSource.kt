package com.insearching.urbansports.gyms.data.networking

import com.insearching.urbansports.core.domain.util.DataError
import com.insearching.urbansports.core.domain.util.Result
import com.insearching.urbansports.gyms.data.networking.dto.GymResponseDto

interface RemoteGymDataSource {
    suspend fun searchOpenGyms(
        resultLimit: Int? = 10
    ): Result<GymResponseDto, DataError.Remote>
}