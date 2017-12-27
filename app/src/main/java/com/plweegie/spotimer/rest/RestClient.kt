package com.plweegie.spotimer.rest

import android.content.Context
import com.google.gson.GsonBuilder
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RestClient(context: Context) {

    var mApiService: SpotifyService

    init {
        val gson = GsonBuilder().create()

        val cache = Cache(context.cacheDir, 5 * 1024 * 1024)
        val client = OkHttpClient.Builder()
                .cache(cache)
                .build()

        val retrofit = Retrofit.Builder()
                .baseUrl(SPOTIFY_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        mApiService = retrofit.create(SpotifyService::class.java)
    }

    companion object {
        const val SPOTIFY_BASE_URL = "https://api.spotify.com/v1/"
        const val TOKEN_PREF = "token_preference"

        fun buildTrackSearchQuery(artistName: String, trackName: String): String {
            return "artist:$artistName track:$trackName"
        }
    }
}