package com.plweegie.spotimer.rest

import android.app.Application
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class NetModule(val mBaseUrl: String) {

    @Provides
    @Singleton
    fun provideCache(application: Application): Cache {
        val cacheSize: Long = 5 * 1024 * 1024
        val cache = Cache(application.cacheDir, cacheSize)
        return cache
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().create()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(cache: Cache): OkHttpClient {
        return OkHttpClient.Builder()
                .cache(cache)
                .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        val retrofit = Retrofit.Builder()
                .baseUrl(this.mBaseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        return retrofit
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): SpotifyService {
        return retrofit.create(SpotifyService::class.java)
    }



}