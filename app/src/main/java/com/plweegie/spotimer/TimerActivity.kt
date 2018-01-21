package com.plweegie.spotimer

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.plweegie.spotimer.extensions.toDurationString
import com.plweegie.spotimer.viewmodels.LiveDataTimerViewModel
import com.plweegie.spotimer.viewmodels.LiveDataTimerViewModelFactory
import kotlinx.android.synthetic.main.activity_timer.*


class TimerActivity : AppCompatActivity() {

    private lateinit var mLiveDataTimerViewModel: LiveDataTimerViewModel

    private var mCounterState: Long? = null
    private var mTrackTempo: Float? = 0.0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        mCounterState = intent.getLongExtra(TRACK_DURATION_EXTRA, 0L)
        mTrackTempo = intent.getFloatExtra(TRACK_TEMPO_EXTRA, 0.0f)

        mCounterState?.let {
            val vmFactory = LiveDataTimerViewModelFactory(it)
            mLiveDataTimerViewModel = ViewModelProviders.of(this, vmFactory)
                    .get(LiveDataTimerViewModel::class.java)
        }
        subscribe()

        startAnimation(mTrackTempo!!, mCounterState!!)
    }

    private fun subscribe() {
        val elapsedTimeObserver = Observer<Long> {
            if (it == -1L) {
                finish()
            }
            timer_textview.text = it?.toDurationString()
        }
        mLiveDataTimerViewModel.getRemainingTime().observe(this, elapsedTimeObserver)
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

        private const val TRACK_DURATION_EXTRA = "track_duration"
        private const val TRACK_TEMPO_EXTRA = "track_tempo_extra"

        fun newIntent(context: Context, trackDuration: Long, trackTempo: Float): Intent? {
            val intent = Intent(context, TimerActivity::class.java)
            intent.putExtra(TRACK_DURATION_EXTRA, trackDuration)
            intent.putExtra(TRACK_TEMPO_EXTRA, trackTempo)
            return intent
        }
    }
}