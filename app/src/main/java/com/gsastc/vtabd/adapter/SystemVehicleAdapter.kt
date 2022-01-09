package com.gsastc.vtabd.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.gsastc.vtabd.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.gsastc.vtabd.model.DataModel
import com.gsastc.vtabd.utils.alertDialog
import com.squareup.picasso.Picasso

class SystemVehicleAdapter(
    private var mContext: Context,
    private var mVehicle: List<DataModel>
) : RecyclerView.Adapter<SystemVehicleAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SystemVehicleAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.system_vehicle_item_list, parent, false)
        return SystemVehicleAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SystemVehicleAdapter.ViewHolder, position: Int) {
        val model = mVehicle[position]
        val vehicleRef = FirebaseDatabase.getInstance().reference.child("vehicle").child(model.vehicleKey!!)
        val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val currentUserID = mAuth.currentUser?.uid
        if (currentUserID == "dyQK5ePVnJVYOF85bcbEX27XUTI2") {
            holder.vehicleVisibility.setOnClickListener {
                if (model.visibility == "Show") {
                    vehicleRef.child("visibility").setValue("Hide")
                }
                else {
                    vehicleRef.child("visibility").setValue("Show")
                }
                holder.vehicleVisibility.text = model.visibility
            }
        }
        Picasso.get().load(model.imageUrl).placeholder(R.drawable.empty)
            .into(holder.vehicleImage)
        holder.vehicleModelName.text = model.vehicleModelNane
        holder.vehicleCapacity.text = model.capacity
        holder.vehiclePrice.text = model.price
        holder.ac.text = model.ac
        holder.vehicleDesc.text = model.description
        holder.vehicleVisibility.text = model.visibility
        holder.delete.setOnClickListener {
            alertDialog(mContext, "Are you want delete this vehicle \nforever?", vehicleRef)
        }

    }

    override fun getItemCount(): Int {
        return mVehicle.size
    }

    class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var vehicleImage: ImageView = itemView.findViewById(R.id.vehicle_image)
        var vehicleCapacity: TextView = itemView.findViewById(R.id.vehicle_capacity)
        var vehicleModelName: TextView = itemView.findViewById(R.id.vehicle_model_name)
        var vehiclePrice: TextView = itemView.findViewById(R.id.vehicle_price)
        var ac: TextView = itemView.findViewById(R.id.ac)
        var vehicleDesc: TextView = itemView.findViewById(R.id.vehicle_desc)
        var vehicleVisibility: TextView = itemView.findViewById(R.id.vehicle_visibility)
        var delete: ImageView = itemView.findViewById(R.id.delete)
    }
}