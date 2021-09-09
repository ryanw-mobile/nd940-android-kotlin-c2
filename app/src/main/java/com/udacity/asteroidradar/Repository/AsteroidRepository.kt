package com.udacity.asteroidradar.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.network.Network
import com.udacity.asteroidradar.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class AsteroidRepository(private val database: AsteroidsDatabase) {

    val asteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroids()) {
            it.asDomainModel()
        }

    suspend fun refreshAsteroids() {
        // TODO: this network call requires query string. This is a hardcode one for dev. use
        val queryString = mutableMapOf<String, String>()
        queryString.put("start_date", "2015-09-07")
        queryString.put("end_date", "2015-09-08")
        queryString.put("start_date", "DEMO_KEY")

        // force the Kotlin coroutine to switch to the IO dispatcher.
        withContext(Dispatchers.IO) {
            val asteroidList = Network.neoWs.getAsteroidListAsync(queryString).await()
            // Note the asterisk * is the spread operator. It allows you to pass in an array to a function that expects varargs.
            database.asteroidDao.insertAll(*asteroidList.asDatabaseModel())
        }
    }
}