package com.insearching.urbansports.gyms.domain.usecase

import com.insearching.urbansports.core.domain.util.DataError
import com.insearching.urbansports.core.domain.util.Result
import com.insearching.urbansports.core.domain.util.map
import com.insearching.urbansports.gyms.domain.GymsRepository
import com.insearching.urbansports.gyms.domain.model.Gym
import com.insearching.urbansports.gyms.utils.Constants

class AddGymToFavorites(
    private val gymRepository: GymsRepository
) {
    suspend operator fun invoke(gym: Gym): Result<Boolean, DataError.Local> {
        return gymRepository.updateGym(gym.copy(isFavorite = true)).map {
            hasMatch()
        }
    }

    private fun hasMatch(): Boolean {
        val randomValue = (1..100).random()
        return randomValue <= Constants.GYM_MATCH_CHANCE
    }
}