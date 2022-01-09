package com.gsastc.vtabd.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.gsastc.vtabd.PlaceDetailsActivity
import com.gsastc.vtabd.R
import com.gsastc.vtabd.model.DataModel
import com.gsastc.vtabd.utils.alertDialog
import com.squareup.picasso.Picasso

class PlaceAdapter (
    private var mContext: Context,
    private var mPlaces: List<DataModel>) : RecyclerView.Adapter<PlaceAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.place_list_item, parent, false)
        return PlaceAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceAdapter.ViewHolder, position: Int) {
        val places = mPlaces[position]
        val mAuth = FirebaseAuth.getInstance()
        val currentUserID = mAuth.currentUser?.uid
        if (currentUserID == "dyQK5ePVnJVYOF85bcbEX27XUTI2") {
            holder.delete.visibility = View.VISIBLE
        }
        else {
            holder.delete.visibility = View.GONE
        }
        holder.placeName.text = places.placeName
        holder.district.text = places.city
        Picasso.get().load(places.imageUrl).placeholder(R.drawable.empty)
            .into(holder.placeImage)
        holder.delete.setOnClickListener {
            alertDialog(mContext, "Are you want to delete this place \nforever?")
        }
        holder.placeImage.setOnClickListener {
            val intent = Intent(mContext, PlaceDetailsActivity::class.java)
            intent.putExtra("imageUrl", places.imageUrl)
            intent.putExtra("placeName", places.placeName)
            intent.putExtra("division", places.division)
            intent.putExtra("city", places.city)
            intent.putExtra("desc", places.description)
            mContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return mPlaces.size
    }

    class ViewHolder (@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var placeName: TextView = itemView.findViewById(R.id.place_name)
        var district: TextView = itemView.findViewById(R.id.place_district)
        var placeImage: ImageView = itemView.findViewById(R.id.place_image)
        var delete: ImageView = itemView.findViewById(R.id.delete)
    }
}