package com.plweegie.spotimer

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.plweegie.spotimer.models.AudioFeatures
import com.plweegie.spotimer.models.SpotifyTrack
import com.plweegie.spotimer.rest.RestClient
import com.plweegie.spotimer.rest.SpotifyService
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var mToken: String
    private lateinit var mPrefs: SharedPreferences
    private lateinit var mService: SpotifyService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mService = RestClient(this).mApiService
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this)

    }

    override fun onStart() {
        super.onStart()
        val builder = AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI)
        builder.setScopes(arrayOf("user-read-private"))
        val request = builder.build()

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE) {
            val response = AuthenticationClient.getResponse(resultCode, data)

            when (response.type) {
                AuthenticationResponse.Type.TOKEN -> {
                    mToken = response.accessToken

                    mPrefs.edit()
                            .putString(RestClient.TOKEN_PREF, mToken)
                            .apply()

                    submit_btn.setOnClickListener {
                        callSpotifyApi(mToken)
                    }
                }

                else -> {
                    //TODO show dialog
                }
            }
        }
    }

    private fun callSpotifyApi(token: String) {

        val artistName = artist_edittext.text.toString()
        val trackName = track_edittext.text.toString()

        val call = mService.getTrackId("Bearer $token", RestClient
                .buildTrackSearchQuery(artistName, trackName))

        call.enqueue(object: Callback<SpotifyTrack> {
            override fun onFailure(call: Call<SpotifyTrack>?, t: Throwable?) {
                Log.e("MainActivity", "Retrofit error " + t)
            }

            override fun onResponse(call: Call<SpotifyTrack>?,
                                    response: Response<SpotifyTrack>?) {

                val items = response?.body()?.tracks?.items
                Log.d("Main Activity", response?.message().toString())
                Log.d("MainActivity", items?.size.toString())
                val trackId = items?.first()!!.id
                Log.d("MainActivity", "Track ID " + trackId)

                val callForDuration = mService.getFeatures("Bearer $token", trackId)

                callForDuration.enqueue(object: Callback<AudioFeatures> {
                    override fun onResponse(call: Call<AudioFeatures>?,
                                            response: Response<AudioFeatures>?) {
                        val trackFeatures = response?.body()
                        val trackDuration = trackFeatures!!.duration
                        val trackTempo = trackFeatures?.tempo

                        val intent = TimerActivity.newIntent(applicationContext,
                                trackId, trackDuration, trackTempo)
                        startActivity(intent)
                    }

                    override fun onFailure(call: Call<AudioFeatures>?, t: Throwable?) {
                        Log.e("MainActivity", "Retrofit error " + t)
                    }
                })
            }
        })
    }

    companion object {
        const val CLIENT_ID = "59df21085b7d453992c02040d5f7eb52"
        const val REDIRECT_URI = "plweegie://spotimer_callback"
        const val REQUEST_CODE = 111
    }
}
