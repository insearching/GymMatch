package com.insearching.urbansports.di

import android.location.Geocoder
import com.google.android.gms.location.LocationServices
import com.insearching.urbansports.MainViewModel
import com.insearching.urbansports.core.data.networking.HttpClientFactory
import com.insearching.urbansports.gyms.data.maps.DefaultLocationManager
import com.insearching.urbansports.gyms.data.networking.KtorRemoteGymDataSource
import com.insearching.urbansports.gyms.data.networking.RemoteGymDataSource
import com.insearching.urbansports.gyms.data.repository.DefaultGymsRepository
import com.insearching.urbansports.gyms.domain.GymsRepository
import com.insearching.urbansports.gyms.domain.LocationManager
import com.insearching.urbansports.gyms.domain.usecase.AddGymToFavorites
import com.insearching.urbansports.gyms.domain.usecase.DislikeGym
import com.insearching.urbansports.gyms.domain.usecase.FindNearbyGyms
import com.insearching.urbansports.gyms.domain.usecase.ObserveCurrentLocation
import com.insearching.urbansports.gyms.presentation.gym_match.MatchingScreenViewModel
import io.ktor.client.engine.cio.CIO
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single { HttpClientFactory.create(CIO.create()) }
    single { Geocoder(androidContext()) }
    single { LocationServices.getFusedLocationProviderClient(androidContext()) }

    singleOf(::KtorRemoteGymDataSource).bind<RemoteGymDataSource>()
    singleOf(::DefaultGymsRepository).bind<GymsRepository>()

    single<LocationManager> { DefaultLocationManager(androidContext(), get(), get()) }

    single { AddGymToFavorites(get()) }
    single { ObserveCurrentLocation(get()) }
    single { DislikeGym(get()) }
    single { FindNearbyGyms(get(), get()) }

    viewModel { MainViewModel() }
    viewModel { MatchingScreenViewModel(get(), get(), get(), get()) }
}