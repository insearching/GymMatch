package com.insearching.urbansports.gyms.presentation.gym_match

import com.insearching.urbansports.gyms.domain.model.Gym

sealed interface MatchingScreenAction {
    data class OnGymLiked(val gym: Gym) : MatchingScreenAction
    data class OnGymDisliked(val gym: Gym) : MatchingScreenAction
    data object OnRefreshGyms : MatchingScreenAction
}