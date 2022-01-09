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
import com.gsastc.vtabd.GuideDetailsActivity
import com.gsastc.vtabd.R
import com.gsastc.vtabd.VehicleDetailsActivity
import com.gsastc.vtabd.model.DataModel
import com.squareup.picasso.Picasso

class TransportAdapter (
    private var mContext: Context,
    private var mTransport: List<DataModel>) : RecyclerView.Adapter<TransportAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransportAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.private_transport_list_item, parent, false)
        return TransportAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransportAdapter.ViewHolder, position: Int) {
        val model = mTransport[position]
        Picasso.get().load(model.imageUrl).placeholder(R.drawable.empty)
            .into(holder.transportImage)
        holder.vehicleModelName.text = model.vehicleModelNane
        holder.address.text = model.cityName
        holder.rant.text = "BDT " + model.price
        holder.desccription.text = model.description
        holder.parentLayout.setOnClickListener {
            val intent = Intent(mContext, VehicleDetailsActivity::class.java)
            intent.putExtra("vehicleKey", model.vehicleKey)
            intent.putExtra("transportID", model.transportID)
            intent.putExtra("imageUrl", model.imageUrl)
            intent.putExtra("transportName", model.transportName)
            intent.putExtra("vehicleModelNane", model.vehicleModelNane)
            intent.putExtra("description", model.description)
            intent.putExtra("cityName", model.cityName)
            intent.putExtra("price", model.price)
            intent.putExtra("ac", model.ac)
            intent.putExtra("capacity", model.capacity)
            mContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return mTransport.size
    }

    class ViewHolder (@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var parentLayout: RelativeLayout = itemView.findViewById(R.id.parent_layout)
        var transportImage: ImageView = itemView.findViewById(R.id.vehicle_image)
        var vehicleModelName: TextView = itemView.findViewById(R.id.vehicle_model_name)
        var address: TextView = itemView.findViewById(R.id.address)
        var rant: TextView = itemView.findViewById(R.id.rant)
        var desccription: TextView = itemView.findViewById(R.id.desc)
    }
}