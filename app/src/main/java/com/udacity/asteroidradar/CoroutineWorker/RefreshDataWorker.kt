package com.udacity.asteroidradar.CoroutineWorker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.ApiKey
import com.udacity.asteroidradar.Repository.AsteroidRepository
import com.udacity.asteroidradar.api.getNextSevenDaysFormattedDates
import com.udacity.asteroidradar.database.getDatabase
import retrofit2.HttpException

class RefreshDataWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val database = getDatabase(applicationContext)
        val repository = AsteroidRepository(database)

        return try {
            // Retrieve an array of formatted dates for the next 7 days
            // and we request to retrieve only today's asteroids from the server
            val nextSevenFormattedDates = getNextSevenDaysFormattedDates()

            // Note to reviewer: ApiKey.NEO_WS is not in this repository. You need to create this data object yourself
            repository.refreshAsteroidsList(
                nextSevenFormattedDates[0],
                nextSevenFormattedDates[0],
                ApiKey.NEO_WS
            )
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }
}
