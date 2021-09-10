package com.udacity.asteroidradar.network

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ImageOfDayObject(
    val url: String,
    val media_type: String,
    val title: String
)