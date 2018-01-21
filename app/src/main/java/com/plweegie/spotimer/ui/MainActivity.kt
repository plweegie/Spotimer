package com.plweegie.spotimer.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.plweegie.spotimer.MyApp
import com.plweegie.spotimer.R
import com.plweegie.spotimer.models.AudioFeatures
import com.plweegie.spotimer.models.SpotifyTrack
import com.plweegie.spotimer.rest.SpotifyService
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private lateinit var mToken: String

    @Inject
    lateinit var mPrefs: SharedPreferences

    @Inject
    lateinit var mService: SpotifyService

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as MyApp).mAppComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
                            .putString(TOKEN_PREF, mToken)
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

    private fun buildTrackSearchQuery(artistName: String, trackName: String): String {
        return "artist:$artistName track:$trackName"
    }

    private fun callSpotifyApi(token: String) {

        val artistName = artist_edittext.text.toString()
        val trackName = track_edittext.text.toString()

        val call = mService.getTrackId("Bearer $token",
                buildTrackSearchQuery(artistName, trackName))

        call.enqueue(object: Callback<SpotifyTrack> {
            override fun onFailure(call: Call<SpotifyTrack>?, t: Throwable?) {
                Log.e("MainActivity", "Retrofit error " + t)
            }

            override fun onResponse(call: Call<SpotifyTrack>?,
                                    response: Response<SpotifyTrack>?) {

                val items = response?.body()?.tracks?.items
                Log.d("Main Activity", response?.toString())
                Log.d("MainActivity", items?.size.toString())
                val trackId = items?.first()!!.id
                Log.d("MainActivity", "Track ID " + trackId)

                val callForDuration = mService.getFeatures("Bearer $token", trackId)

                callForDuration.enqueue(object: Callback<AudioFeatures> {
                    override fun onResponse(call: Call<AudioFeatures>?,
                                            response: Response<AudioFeatures>?) {
                        val trackFeatures = response?.body()
                        val trackDuration = trackFeatures!!.duration
                        val trackTempo = trackFeatures.tempo

                        val intent = TimerActivity.newIntent(applicationContext,
                                trackDuration, trackTempo)
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
        const val TOKEN_PREF = "token_preference"
        const val REDIRECT_URI = "plweegie://spotimer_callback"
        const val REQUEST_CODE = 111
    }
}
