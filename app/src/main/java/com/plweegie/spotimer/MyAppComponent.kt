package com.plweegie.spotimer

import com.plweegie.spotimer.rest.NetModule
import com.plweegie.spotimer.rest.SharedPrefModule
import com.plweegie.spotimer.ui.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(MyAppModule::class, NetModule::class, SharedPrefModule::class))
interface MyAppComponent {
    fun inject(activity: MainActivity)
}