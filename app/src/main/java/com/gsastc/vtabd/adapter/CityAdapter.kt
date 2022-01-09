package com.gsastc.vtabd.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.gsastc.vtabd.R
import com.gsastc.vtabd.SearchResultPlaceListActivity
import com.gsastc.vtabd.model.DataModel
import com.squareup.picasso.Picasso

class CityAdapter (
    private var mContext: Context,
    private var mPlaces: List<DataModel>) : RecyclerView.Adapter<CityAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.city_list_item, parent, false)
        return CityAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CityAdapter.ViewHolder, position: Int) {
        val places = mPlaces[position]

        Picasso.get().load(places.imageUrl).placeholder(R.drawable.loading).into(holder.cityImage)
        holder.division.text = places.division + " Division"
        holder.cityName.text = places.cityName
        holder.city.setOnClickListener {
            val intent = Intent(mContext, SearchResultPlaceListActivity::class.java)
            intent.putExtra("city", places.cityName)
            mContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return mPlaces.size
    }

    class ViewHolder (@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var city: RelativeLayout = itemView.findViewById(R.id.city)
        var cityName: TextView = itemView.findViewById(R.id.city_name)
        var division: TextView = itemView.findViewById(R.id.division_name)
        var cityImage: ImageView = itemView.findViewById(R.id.city_image)
    }

}