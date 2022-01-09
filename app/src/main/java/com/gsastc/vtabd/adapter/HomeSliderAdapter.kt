package com.gsastc.vtabd.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.islamkhsh.CardSliderAdapter
import com.gsastc.vtabd.R
import com.gsastc.vtabd.model.DataModel
import com.gsastc.vtabd.model.HomeSliderModel
import kotlinx.android.synthetic.main.home_slider_content.view.*

class HomeSliderAdapter(private val movies: List<HomeSliderModel>) : CardSliderAdapter<HomeSliderAdapter.MovieViewHolder>() {


    override fun bindVH(holder: MovieViewHolder, position: Int) {

        val movie = movies[position]

        holder.itemView.run {
            Glide.with(context).load(movie.poster)
                .placeholder(R.drawable.empty).error(R.drawable.empty)
                .into(movie_poster)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.home_slider_content, parent, false)

        return MovieViewHolder(view)
    }

    override fun getItemCount() = movies.size


    class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view)
}