package com.plweegie.spotimer.viewmodels

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider


class LiveDataTimerViewModelFactory(val initialTime: Long) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LiveDataTimerViewModel(initialTime) as T
    }
}