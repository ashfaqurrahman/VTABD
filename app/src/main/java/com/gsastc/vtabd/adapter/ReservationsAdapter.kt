package com.gsastc.vtabd.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.gsastc.vtabd.R
import com.gsastc.vtabd.model.DataModel


class ReservationsAdapter(
    private var mContext: Context,
    private var dataModel: List<DataModel>
) : RecyclerView.Adapter<ReservationsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationsAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.order_list_item, parent, false)
        return ReservationsAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReservationsAdapter.ViewHolder, position: Int) {
        val model = dataModel[position]

        holder.userName.text = model.userName
        holder.userAddress.text = model.address
        holder.checkInCheckOut.text = model.checkInCheckOut + " (" + model.days + " day)"
        holder.userPhone.text = model.phone
        holder.call.setOnClickListener {
            val phone = model.phone.toString()
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${phone}")
            mContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return dataModel.size
    }

    class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userName: TextView = itemView.findViewById(R.id.user_name)
        var userAddress: TextView = itemView.findViewById(R.id.address)
        var checkInCheckOut: TextView = itemView.findViewById(R.id.check_in_check_out)
        var userPhone: TextView = itemView.findViewById(R.id.phone)
        var call: ImageView = itemView.findViewById(R.id.call)
    }
}