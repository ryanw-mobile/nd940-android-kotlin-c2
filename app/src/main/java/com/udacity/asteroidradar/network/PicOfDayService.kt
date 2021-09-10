package com.udacity.asteroidradar.network

import retrofit2.http.GET
import retrofit2.http.Query

interface PicOfDayService {
    @GET("/planetary/apod")
    suspend fun getPicOfDay(
        @Query("api_key") apiKey: String
    ): ImageOfDayObject
}