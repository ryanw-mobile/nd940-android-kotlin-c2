package com.udacity.asteroidradar.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service to retrive a list of Asteroids based on their cloest approach date to Earth
 * Query map we expect the following parameters:
 * start_date   YYYY-MM-DD  default: none                       Starting date for asteroid search
 * end_date     YYYY-MM-DD  default: 7 days after start_date    Ending date for asteroid search
 * api_key      string      default:DEMO_KEY                    api.nasa.gov key for expanded usage
 */
interface NeoWsService {
    @GET("/neo/rest/v1/feed")
    suspend fun getAsteroidList(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("api_key") apiKey: String
    ): Response<String>
}