package com.udacity.asteroidradar.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.ApiKey
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.network.NetworkAsteroidContainer
import com.udacity.asteroidradar.network.NetworkScalars
import com.udacity.asteroidradar.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject


class AsteroidRepository(private val database: AsteroidsDatabase) {

    val asteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroids()) {
            it.asDomainModel()
        }

    suspend fun refreshAsteroidsList(startDate :String, endDate :String, apiKey :String) {
        // force the Kotlin coroutine to switch to the IO dispatcher.
        withContext(Dispatchers.IO) {
            val responseText =
                NetworkScalars.neoWs.getAsteroidList(startDate, endDate, apiKey).body()

            // If the response is empty, we skip processing the data
            if (!responseText.isNullOrEmpty()) {
                val asteroidList = NetworkAsteroidContainer(
                    parseAsteroidsJsonResult(JSONObject(responseText))
                )

                // Note the asterisk * is the spread operator. It allows you to pass in an array to a function that expects varargs.
                database.asteroidDao.insertAll(*asteroidList.asDatabaseModel())
            }
        }
    }
}