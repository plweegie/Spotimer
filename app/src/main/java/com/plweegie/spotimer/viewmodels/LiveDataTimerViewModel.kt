package com.plweegie.spotimer.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.os.CountDownTimer


class LiveDataTimerViewModel(val initialTime: Long) : ViewModel() {

    private var mRemainingTime: MutableLiveData<Long> = MutableLiveData()

    init {
        val timer = object: CountDownTimer(initialTime, 500) {
            override fun onFinish() {
                mRemainingTime.postValue(-1L)
            }

            override fun onTick(millisUntilFinished: Long) {
                mRemainingTime.postValue(millisUntilFinished)
            }
        }.start()
    }

    fun getRemainingTime(): LiveData<Long> {
        return  mRemainingTime
    }
}