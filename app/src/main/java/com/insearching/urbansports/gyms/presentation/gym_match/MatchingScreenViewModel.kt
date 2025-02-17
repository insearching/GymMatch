package com.insearching.urbansports.gyms.presentation.gym_match


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.insearching.urbansports.core.domain.util.onError
import com.insearching.urbansports.core.domain.util.onSuccess
import com.insearching.urbansports.core.presentation.util.UiText
import com.insearching.urbansports.core.presentation.util.toUiText
import com.insearching.urbansports.gyms.domain.model.GeoPoint
import com.insearching.urbansports.gyms.domain.usecase.AddGymToFavorites
import com.insearching.urbansports.gyms.domain.usecase.DislikeGym
import com.insearching.urbansports.gyms.domain.usecase.FindNearbyGyms
import com.insearching.urbansports.gyms.domain.usecase.ObserveCurrentLocation
import com.insearching.urbansports.R
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MatchingScreenViewModel(
    private val addGymToFavorites: AddGymToFavorites,
    private val dislikeGym: DislikeGym,
    private val observeCurrentLocation: ObserveCurrentLocation,
    private val findNearbyGyms: FindNearbyGyms
) : ViewModel() {

    private val _state = MutableStateFlow<MatchingScreenState>(MatchingScreenState.Loading)
    val state = _state
        .onStart {
            checkLocation()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )

    private val _matchChannel = Channel<Any>()
    val matchChannel = _matchChannel.receiveAsFlow()

    private fun checkLocation() {
        _state.update {
            MatchingScreenState.Loading
        }

        viewModelScope.launch {
            observeCurrentLocation()
                .collectLatest {
                    it.onSuccess { location ->
                        searchForGyms(location)
                    }.onError { error ->
                        _state.update {
                            MatchingScreenState.Error(error.toUiText())
                        }
                    }
                }
        }
    }

    private fun searchForGyms(currentLocation: GeoPoint) {
        _state.update {
            MatchingScreenState.Loading
        }

        viewModelScope.launch {
            findNearbyGyms(currentLocation)
                .filterNotNull()
                .collectLatest { result ->
                    result.onSuccess { gyms ->
                        _state.update {
                            if (gyms.isEmpty()) {
                                MatchingScreenState.Error(UiText.StringResource(R.string.no_gyms_found))
                            } else {
                                MatchingScreenState.Success(gyms)
                            }
                        }
                    }.onError { error ->
                        _state.update {
                            MatchingScreenState.Error(error.toUiText())
                        }
                    }
                }
        }
    }

    fun onAction(action: MatchingScreenAction) {
        when (action) {
            is MatchingScreenAction.OnGymLiked -> {
                viewModelScope.launch {
                    addGymToFavorites(action.gym)
                        .onSuccess { hasMatch ->
                            if (hasMatch) {
                                _matchChannel.send(Any())
                            }
                        }
                        .onError { error ->
                            _state.update {
                                MatchingScreenState.Error(error.toUiText())
                            }
                        }
                }
            }

            is MatchingScreenAction.OnGymDisliked -> {
                viewModelScope.launch {
                    dislikeGym(action.gym)
                }
            }

            is MatchingScreenAction.OnRefreshGyms -> {
                checkLocation()
            }
        }
    }
}
