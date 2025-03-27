package com.insearching.urbansports.gyms.presentation.gym_match


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.insearching.urbansports.R
import com.insearching.urbansports.core.domain.util.getOrDefault
import com.insearching.urbansports.core.domain.util.isSucceeded
import com.insearching.urbansports.core.domain.util.onError
import com.insearching.urbansports.core.domain.util.onSuccess
import com.insearching.urbansports.core.presentation.util.UiText
import com.insearching.urbansports.core.presentation.util.toUiText
import com.insearching.urbansports.gyms.domain.model.GeoPoint
import com.insearching.urbansports.gyms.domain.usecase.AddGymToFavorites
import com.insearching.urbansports.gyms.domain.usecase.DislikeGym
import com.insearching.urbansports.gyms.domain.usecase.FindNearbyGyms
import com.insearching.urbansports.gyms.domain.usecase.ObserveCurrentLocation
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class MatchingScreenViewModel(
    private val addGymToFavorites: AddGymToFavorites,
    private val dislikeGym: DislikeGym,
    private val findNearbyGyms: FindNearbyGyms,
    private val observeCurrentLocation: ObserveCurrentLocation,
) : ViewModel() {

    private val _state = MutableStateFlow<MatchingScreenState>(MatchingScreenState.Loading)
    val state = _state
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000L),
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
                .flatMapLatest {
                    flow {
                        it.onSuccess { location ->
                            emitAll(searchForGyms(location))
                        }.onError { error ->
                            emit(MatchingScreenState.Error(error.toUiText()))
                        }
                    }
                }.collectLatest { state ->
                    _state.update { state }
                }
        }
    }

    private fun searchForGyms(currentLocation: GeoPoint): Flow<MatchingScreenState> = flow {
        emit(MatchingScreenState.Loading)

        val flow = findNearbyGyms(currentLocation)
            .filterNotNull()
            .map { result ->
                if (result.isSucceeded()) {
                    val gyms = result.getOrDefault(emptyList())
                    if (gyms.isEmpty()) {
                        MatchingScreenState.Error(UiText.StringResource(R.string.no_gyms_found))
                    } else {
                        MatchingScreenState.Success(gyms)
                    }
                } else {
                    MatchingScreenState.Error(UiText.StringResource(R.string.no_gyms_found))
                }
            }
        emitAll(flow)
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
