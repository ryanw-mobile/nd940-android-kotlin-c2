package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.ApiKey
import com.udacity.asteroidradar.Repository.AsteroidRepository
import com.udacity.asteroidradar.api.getNextSevenDaysFormattedDates
import com.udacity.asteroidradar.database.getDatabase
import kotlinx.coroutines.launch

enum class ListFilter { ALL, TODAY, WEEK }

class MainViewModel(application: Application) : ViewModel() {

    private val database = getDatabase(application)
    private val asteroidRepository = AsteroidRepository(database)
    val asteroidList = asteroidRepository.asteroids

    init {
        updateFilter(ListFilter.WEEK)
    }

    // Update the filter settings, and also try to pull some asteroids
    // to make sure we always cover the full range of data
    fun updateFilter(filterSetting: ListFilter) {
        when (filterSetting) {
            ListFilter.TODAY -> {
                val nextSevenFormattedDates = getNextSevenDaysFormattedDates()
                asteroidRepository.requestFilterToday(nextSevenFormattedDates[0])
                refreshRepository()
            }
            ListFilter.WEEK -> {
                val nextSevenFormattedDates = getNextSevenDaysFormattedDates()
                asteroidRepository.requestFilterRange(
                    nextSevenFormattedDates[0],
                    nextSevenFormattedDates[6]
                )
                refreshRepository()
            }
            else -> asteroidRepository.requestFilterAll()
        }
    }

    private fun refreshRepository() {
        viewModelScope.launch {
            // Retrieve an array of formatted dates for the next 7 days
            // and we request to retrieve 7 days' asteroids from the server
            val nextSevenFormattedDates = getNextSevenDaysFormattedDates()

            // Note to reviewer: ApiKey.NEO_WS is not in this repository. You need to create this data object yourself
            asteroidRepository.refreshAsteroidsList(
                nextSevenFormattedDates[0],
                nextSevenFormattedDates[6],
                ApiKey.NEO_WS,
                false
            )
        }
    }
}