package com.insearching.urbansports.gyms.domain.usecase

import com.insearching.urbansports.core.domain.util.DataError
import com.insearching.urbansports.core.domain.util.Result
import com.insearching.urbansports.core.domain.util.map
import com.insearching.urbansports.gyms.domain.GymsRepository
import com.insearching.urbansports.gyms.domain.model.Gym
import com.insearching.urbansports.gyms.utils.Constants

/**
 * Use case for adding a gym to the user's favorites list.
 *
 * This class encapsulates the logic for marking a gym as a favorite in the data layer.
 * It interacts with the [GymsRepository] to update the gym's `isFavorite` status.
 * It also incorporates a simulated "match" chance when adding a gym to favorites.
 *
 * @property gymRepository The repository responsible for managing gym data.
 */
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