package com.udacity.asteroidradar.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
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
import timber.log.Timber


class AsteroidRepository(private val database: AsteroidsDatabase) {

    var _asteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAll()) {
            Timber.d("1")
            it.asDomainModel()
        }
    val asteroids
        get() = _asteroids

    /**
     * Without triggering a new download, just to apply SQL filter to change what to show on the recyclerview
     */
    fun requestFilterAll() {
        Timber.d("Showing all asteroids")
        _asteroids = Transformations.map(database.asteroidDao.getAll()) {
            Timber.d("2")
            it.asDomainModel()
        }
    }

    fun requestFilterToday(startDate: String) {
        Timber.d("Showing asteroids: $startDate")
        _asteroids = Transformations.map(database.asteroidDao.getAsteroidsByDate(startDate)) {
            Timber.d("3")
            it.asDomainModel()
        }
    }

    fun requestFilterRange(startDate: String, endDate: String) {
        Timber.d("Showing asteroids: $startDate to $endDate")
        _asteroids = Transformations.switchMap(database.asteroidDao.getAsteroidsByRange(startDate, endDate))
    }

    suspend fun refreshAsteroidsList(
        startDate: String,
        endDate: String,
        apiKey: String,
        deletePastAsteroids: Boolean
    ) {
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

            // Additional function - if true, the asteroids before the given startDate will be deleted
            if (deletePastAsteroids) {
                database.asteroidDao.deletePastAsteroids(startDate)
            }
        }
    }
}