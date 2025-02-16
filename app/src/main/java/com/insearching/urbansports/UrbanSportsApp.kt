package com.insearching.urbansports

import android.app.Application
import com.insearching.urbansports.di.appModule
import com.insearching.urbansports.di.databaseModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class UrbanSportsApp: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@UrbanSportsApp)
            androidLogger()

            modules(appModule, databaseModule)
        }
    }
}