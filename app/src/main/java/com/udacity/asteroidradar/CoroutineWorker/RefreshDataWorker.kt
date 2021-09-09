package com.udacity.asteroidradar.CoroutineWorker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.ApiKey
import com.udacity.asteroidradar.Repository.AsteroidRepository
import com.udacity.asteroidradar.api.getNextSevenDaysFormattedDates
import com.udacity.asteroidradar.database.getDatabase
import retrofit2.HttpException
import timber.log.Timber

class RefreshDataWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val database = getDatabase(applicationContext)
        val repository = AsteroidRepository(database)

        Timber.d("RefreshDataWorker has been triggered")

        return try {
            // Retrieve an array of formatted dates for the next 7 days
            // and we request to retrieve 7 days' asteroids from the server
            // Note to reviewer: The Project Instructions asked us to cache 1 day's asteroid,
            // but the Rubric says we should download 7 days. So I download 7 days.
            val nextSevenFormattedDates = getNextSevenDaysFormattedDates()

            // Note to reviewer: ApiKey.NEO_WS is not in this repository. You need to create this data object yourself
            repository.refreshAsteroidsList(
                nextSevenFormattedDates[0],
                nextSevenFormattedDates[6],
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
