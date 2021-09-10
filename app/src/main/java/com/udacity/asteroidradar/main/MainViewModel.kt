package com.udacity.asteroidradar.main

import android.app.Application
import android.media.Image
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.ApiKey
import com.udacity.asteroidradar.Repository.AsteroidRepository
import com.udacity.asteroidradar.api.getNextSevenDaysFormattedDates
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.network.ImageOfDayObject
import com.udacity.asteroidradar.network.NetworkMoshi
import kotlinx.coroutines.launch
import retrofit2.HttpException


class MainViewModel(application: Application) : ViewModel() {

    private val database = getDatabase(application)
    private val asteroidRepository = AsteroidRepository(database)
    val asteroidList = asteroidRepository.asteroids
    private var _imageOfDayObject = MutableLiveData<ImageOfDayObject>()
    val imageOfDayObject: LiveData<ImageOfDayObject>
        get() = _imageOfDayObject

    init {
        viewModelScope.launch {
            // Retrieve an array of formatted dates for the next 7 days
            // and we request to retrieve 7 days' asteroids from the server
            val nextSevenFormattedDates = getNextSevenDaysFormattedDates()

            try {
                // Get Pic of the Day
                // Note to reviewer: ApiKey.NEO_WS is not in this repository. You need to create this data object yourself
                _imageOfDayObject.value = NetworkMoshi.apodWs.getPicOfDay(ApiKey.NEO_WS)

            } catch (e: HttpException) {
                // This occurs when there is no network, but can be due to other network error
                e.printStackTrace()
            } catch (e: java.net.UnknownHostException) {
                // This occurs when there is no network, but can be due to other network error
                e.printStackTrace()
            }

            try {
                // Refresh database
                asteroidRepository.refreshAsteroidsList(
                    nextSevenFormattedDates[0],
                    nextSevenFormattedDates[6],
                    ApiKey.NEO_WS,
                    false
                )
            } catch (e: HttpException) {
                // This occurs when there is no network, but can be due to other network error
                e.printStackTrace()
            } catch (e: java.net.UnknownHostException) {
                // This occurs when there is no network, but can be due to other network error
                e.printStackTrace()
            }
        }
    }
}