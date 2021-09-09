package com.udacity.asteroidradar.main

import com.udacity.asteroidradar.domain.Asteroid

/***
 * Handles the click event on the recyclerview.
 * It can also handle other callbacks but right now we just have onclick
 */
class AsteroidClickListener(val selectedAsteroid: (Asteroid) -> Unit) {
    fun onClick(asteroid: Asteroid) = selectedAsteroid(asteroid)
}