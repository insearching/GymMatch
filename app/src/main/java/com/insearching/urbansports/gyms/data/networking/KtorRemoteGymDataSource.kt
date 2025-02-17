package com.insearching.urbansports.gyms.data.networking

import com.insearching.urbansports.core.data.networking.constructUrl
import com.insearching.urbansports.core.data.networking.safeCall
import com.insearching.urbansports.core.domain.util.DataError
import com.insearching.urbansports.core.domain.util.Result
import com.insearching.urbansports.gyms.data.networking.dto.GymResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class KtorRemoteGymDataSource(
    private val httpClient: HttpClient
) : RemoteGymDataSource {

    override suspend fun searchOpenGyms(
        resultLimit: Int?
    ): Result<GymResponseDto, DataError.Remote> = withContext(Dispatchers.IO) {
        safeCall<GymResponseDto> {
            httpClient.get(
                urlString = constructUrl("/catalog/datasets/open-gym/records")
            ) {
                parameter("limit", resultLimit)
            }
        }
    }
}