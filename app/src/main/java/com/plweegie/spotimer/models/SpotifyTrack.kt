package com.plweegie.spotimer.models

import com.google.gson.annotations.SerializedName


data class SpotifyTrack(@SerializedName("tracks") val tracks: TracksObject) {

    inner class TracksObject(@SerializedName("items") val items: List<ItemObject>) {

        inner class ItemObject(@SerializedName("id") val id: String)

    }
}
