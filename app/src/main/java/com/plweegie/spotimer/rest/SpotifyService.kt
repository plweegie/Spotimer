package com.plweegie.spotimer.rest

import com.plweegie.spotimer.models.AudioFeatures
import com.plweegie.spotimer.models.SpotifyTrack
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query


interface SpotifyService {

    @GET("search/?type=track")
    fun getTrackId(@Header("Authorization") auth: String,
                   @Query("q", encoded = true) searchInput: String): Call<SpotifyTrack>

    @GET("audio-features/{id}")
    fun getFeatures(@Header("Authorization") auth: String,
                    @Path("id") trackId: String): Call<AudioFeatures>

}