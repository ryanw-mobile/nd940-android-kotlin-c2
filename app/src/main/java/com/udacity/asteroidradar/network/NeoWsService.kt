package com.udacity.asteroidradar.network

import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.QueryMap

/**
 * Retrofit service to retrive a list of Asteroids based on their cloest approach date to Earth
 * Query map we expect the following parameters:
 * start_date   YYYY-MM-DD  default: none                       Starting date for asteroid search
 * end_date     YYYY-MM-DD  default: 7 days after start_date    Ending date for asteroid search
 * api_key      string      default:DEMO_KEY                    api.nasa.gov key for expanded usage
 */
interface NeoWsService {
    @GET("/neo/rest/v1/feed")
    fun getAsteroidListAsync(@QueryMap options: Map<String, String>):
            Deferred<NetworkAsteroidContainer>
}