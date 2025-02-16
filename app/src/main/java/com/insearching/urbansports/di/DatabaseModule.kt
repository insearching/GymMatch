package com.insearching.urbansports.di

import com.insearching.urbansports.gyms.data.database.DatabaseFactory
import com.insearching.urbansports.gyms.data.database.GymDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        DatabaseFactory(androidContext()).create().build()
    }

    single { get<GymDatabase>().gymDao() }
}
