package com.plweegie.spotimer

import android.app.Application
import com.plweegie.spotimer.rest.NetModule
import com.plweegie.spotimer.rest.SharedPrefModule


class MyApp : Application() {

    val mAppComponent: MyAppComponent by lazy {
        DaggerMyAppComponent.builder()
                .myAppModule(MyAppModule(this))
                .netModule(NetModule(SPOTIFY_BASE_URL))
                .sharedPrefModule(SharedPrefModule())
                .build()
    }


    override fun onCreate() {
        super.onCreate()
    }

    companion object {
        const val SPOTIFY_BASE_URL = "https://api.spotify.com/v1/"
    }
}