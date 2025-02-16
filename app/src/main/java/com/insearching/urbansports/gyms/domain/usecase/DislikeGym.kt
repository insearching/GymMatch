package com.insearching.urbansports.gyms.domain.usecase

import com.insearching.urbansports.core.domain.util.DataError
import com.insearching.urbansports.core.domain.util.EmptyResult
import com.insearching.urbansports.gyms.domain.GymsRepository
import com.insearching.urbansports.gyms.domain.model.Gym

/**
 * `DislikeGym` is a Use Case responsible for marking a specific gym as disliked or skipped.
 *
 * This class provides a single operation (`invoke`) that updates the provided [Gym] in the
 * underlying data repository, setting its `isSkipped` property to `true`. This effectively
 * signifies that the user dislikes or wants to skip this particular gym.
 *
 * @property gymRepository The repository used to interact with the underlying data storage
 *                        for Gym entities. It is responsible for persisting the updated state
 *                        of the gym (i.e., marking it as skipped).
 */
class DislikeGym(
    private val gymRepository: GymsRepository
) {
    suspend operator fun invoke(gym: Gym): EmptyResult<DataError.Local> {
        return gymRepository.updateGym(gym.copy(isSkipped = true))
    }
}