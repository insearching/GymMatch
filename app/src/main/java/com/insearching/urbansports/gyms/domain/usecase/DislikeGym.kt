package com.insearching.urbansports.gyms.domain.usecase

import com.insearching.urbansports.core.domain.util.DataError
import com.insearching.urbansports.core.domain.util.EmptyResult
import com.insearching.urbansports.gyms.domain.GymsRepository
import com.insearching.urbansports.gyms.domain.model.Gym

class DislikeGym(
    private val gymRepository: GymsRepository
) {
    suspend operator fun invoke(gym: Gym): EmptyResult<DataError.Local> {
        return gymRepository.updateGym(gym.copy(isSkipped = true))
    }
}