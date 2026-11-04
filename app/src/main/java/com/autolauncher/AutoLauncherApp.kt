package com.autolauncher

import android.app.Application
import com.autolauncher.data.AppRepository
import com.autolauncher.data.Preferences
import com.autolauncher.data.VehicleRepository

class AutoLauncherApp : Application() {

    val prefs: Preferences by lazy { Preferences(this) }
    val appRepository: AppRepository by lazy { AppRepository(this) }
    val vehicleRepository: VehicleRepository by lazy { VehicleRepository() }

    companion object {
        lateinit var instance: AutoLauncherApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
