package com.udacity.asteroidradar.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ImageOfDayObject(
    val url: String,
    @Json(name = "media_type")
    val mediaType: String,
    val title: String
)