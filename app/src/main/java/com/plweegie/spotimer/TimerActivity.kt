package com.plweegie.spotimer

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.plweegie.spotimer.extensions.toDurationString
import com.plweegie.spotimer.models.AudioFeatures
import com.plweegie.spotimer.rest.RestClient
import com.plweegie.spotimer.rest.SpotifyService
import kotlinx.android.synthetic.main.activity_timer.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TimerActivity : AppCompatActivity() {

    private lateinit var mToken: String
    private lateinit var mService: SpotifyService
    private lateinit var mTrackId: String
    private var mCounterState: Long? = null
    private var mCounting: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        mCounting = savedInstanceState?.getBoolean(COUNTING_STATE) ?: false

        if (mCounting!!) {
            mCounterState = savedInstanceState?.getLong(COUNTER_POSITION)
        }

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        mToken = prefs.getString(RestClient.TOKEN_PREF, null)
        mTrackId = intent.getStringExtra(TRACK_ID_EXTRA)

        mService = RestClient(this).mApiService
    }

    override fun onStart() {
        super.onStart()
        if (!mCounting!!) {
            getTrackInfoFromAPI(mToken)
        } else {
            startTimer(mCounterState!!)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putBoolean(COUNTING_STATE, mCounting!!)
        outState?.putLong(COUNTER_POSITION, mCounterState!!)
    }

    private fun getTrackInfoFromAPI(token: String) {

        val call = mService.getFeatures("Bearer $token", mTrackId)

        call.enqueue(object: Callback<AudioFeatures> {
            override fun onResponse(call: Call<AudioFeatures>?, response: Response<AudioFeatures>?) {
                val trackFeatures = response?.body()
                mCounterState = trackFeatures!!.duration
                val tempo = trackFeatures?.tempo

                mCounting = true
                startTimer(mCounterState!!)
                startAnimation(tempo, mCounterState!!)
            }

            override fun onFailure(call: Call<AudioFeatures>?, t: Throwable?) {
                Log.e("MainActivity", "Retrofit error " + t)
            }
        })
    }

    private fun startTimer(duration: Long) {
       val timer = object: CountDownTimer(duration, 1000) {
           override fun onFinish() {
               mCounting = false
               finish()
           }

           override fun onTick(millisUntilFinished: Long) {
               timer_textview.text = millisUntilFinished.toDurationString()
               mCounterState = millisUntilFinished
           }
       }.start()
    }

    private fun startAnimation(tempo: Float, duration: Long) {

        val halfBeatDuration = (30000 / tempo)

        val xValuesHolder = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 0.333f)
        val yValuesHolder = PropertyValuesHolder.ofFloat("scaleY", 1.0f, 0.333f)

        val shrinkAnimator = ObjectAnimator
                .ofPropertyValuesHolder(tempo_animator, xValuesHolder, yValuesHolder)
                .setDuration(Math.round(halfBeatDuration).toLong())

        shrinkAnimator.apply {
            repeatCount = Math.round(duration / halfBeatDuration.toDouble()).toInt()
            repeatMode = ObjectAnimator.REVERSE
        }

        shrinkAnimator.start()
    }

    companion object {

        const val TRACK_ID_EXTRA = "track_id"
        const val COUNTING_STATE = "is_counting"
        const val COUNTER_POSITION = "counter_position"

        fun newIntent(context: Context, trackId: String?): Intent? {
            val intent = Intent(context, TimerActivity::class.java)
            intent.putExtra(TRACK_ID_EXTRA, trackId)
            return intent
        }
    }
}