package com.udacity.asteroidradar.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.ListitemAsteroidBinding
import com.udacity.asteroidradar.domain.Asteroid

/**
 * For the RecyclerView on the main screen.
 */
class AsteroidAdapter(val clickListener: AsteroidClickListener) :
    RecyclerView.Adapter<AsteroidViewHolder>() {

    var asteroids: List<Asteroid> = emptyList()
        set(value) {
            field = value
            // This is not a very good way to refresh RecyclerView.
            // TODO: Use DiffUtils
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsteroidViewHolder {
        val withDataBinding: ListitemAsteroidBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            AsteroidViewHolder.LAYOUT,
            parent,
            false
        )
        return AsteroidViewHolder(withDataBinding)
    }

    override fun getItemCount() = asteroids.size

    override fun onBindViewHolder(holder: AsteroidViewHolder, position: Int) {
        holder.viewDataBinding.also {
            // These correspond to the data variables we defined in the XML
            it.asteroid = asteroids[position]
            it.asteroidClickListener = clickListener
        }
    }

}

/**
 * ViewHolder for the RecyclerView.
 * The XML layout has been converted to use data binding,
 * therefore we just link it up with RecyclerView
 */
class AsteroidViewHolder(val viewDataBinding: ListitemAsteroidBinding) :
    RecyclerView.ViewHolder(viewDataBinding.root) {
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.listitem_asteroid
    }
}
