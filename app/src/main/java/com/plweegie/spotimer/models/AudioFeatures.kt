package com.plweegie.spotimer.models

import com.google.gson.annotations.SerializedName


data class AudioFeatures(@SerializedName("duration_ms") val duration: Long,
                         @SerializedName("tempo") val tempo: Float) {
}