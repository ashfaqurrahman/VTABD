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
import com.google.firebase.auth.FirebaseAuth
import com.gsastc.vtabd.HotelDetailsActivity
import com.gsastc.vtabd.R
import com.gsastc.vtabd.model.DataModel
import com.gsastc.vtabd.utils.alertDialog
import com.squareup.picasso.Picasso

class HotelAdapter (
    private var mContext: Context,
    private var mHotel: List<DataModel>) : RecyclerView.Adapter<HotelAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotelAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.hotel_list_item, parent, false)
        return HotelAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: HotelAdapter.ViewHolder, position: Int) {
        val hotel = mHotel[position]
        Picasso.get().load(hotel.imageUrl).placeholder(R.drawable.empty)
            .into(holder.hotelImage)
        holder.hotelName.text = hotel.hotelName
        holder.hotelDistrict.text = hotel.cityName
        holder.desc.text = hotel.hotelDescription
        holder.parentLayout.setOnClickListener {
            val intent = Intent(mContext, HotelDetailsActivity::class.java)
            intent.putExtra("hotelID", hotel.hotelID)
            mContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return mHotel.size
    }

    class ViewHolder (@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var parentLayout: RelativeLayout = itemView.findViewById(R.id.parent_layout)
        var hotelName: TextView = itemView.findViewById(R.id.hotel_name)
        var hotelDistrict: TextView = itemView.findViewById(R.id.hotel_district)
        var desc: TextView = itemView.findViewById(R.id.description)
        var hotelImage: ImageView = itemView.findViewById(R.id.hotel_image)
    }
}