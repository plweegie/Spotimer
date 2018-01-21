package com.plweegie.spotimer

import android.app.Application
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class MyAppModule(val mApplication: Application) {

    @Provides
    @Singleton
    fun providesApplication(): Application {
        return mApplication
    }
}