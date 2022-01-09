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
import com.google.firebase.database.FirebaseDatabase
import com.gsastc.vtabd.R
import com.gsastc.vtabd.SearchResultPlaceListActivity
import com.gsastc.vtabd.model.DataModel
import com.gsastc.vtabd.utils.alertDialog
import com.squareup.picasso.Picasso

class SystemDistrictAdapter (
    private var mContext: Context,
    private var mPlaces: List<DataModel>) : RecyclerView.Adapter<SystemDistrictAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SystemDistrictAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.system_district_list_item, parent, false)
        return SystemDistrictAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SystemDistrictAdapter.ViewHolder, position: Int) {
        val model = mPlaces[position]
        val districtRef = FirebaseDatabase.getInstance().reference.child(model.districtKey!!)
        Picasso.get().load(model.imageUrl).placeholder(R.drawable.loading).into(holder.districtImage)
        holder.districtName.text = model.districtName
        holder.description.text = model.description
        holder.delete.setOnClickListener {
            alertDialog(mContext, "Are want to delete this district \nforever?", districtRef)
        }
    }

    override fun getItemCount(): Int {
        return mPlaces.size
    }

    class ViewHolder (@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var districtImage: ImageView = itemView.findViewById(R.id.district_image)
        var districtName: TextView = itemView.findViewById(R.id.district_name)
        var description: TextView = itemView.findViewById(R.id.description)
        var delete: ImageView = itemView.findViewById(R.id.delete)
    }

}