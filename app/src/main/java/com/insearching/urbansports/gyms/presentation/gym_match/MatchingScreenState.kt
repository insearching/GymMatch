package com.insearching.urbansports.gyms.presentation.gym_match

import com.insearching.urbansports.core.presentation.util.UiText
import com.insearching.urbansports.gyms.domain.model.Gym

sealed interface MatchingScreenState {
    data object Loading: MatchingScreenState
    data class Error(val message: UiText): MatchingScreenState
    data class Success(val data: List<Gym>): MatchingScreenState
}