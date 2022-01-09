package com.gsastc.vtabd.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.gsastc.vtabd.R
import com.gsastc.vtabd.RoomDetailsActivity
import com.gsastc.vtabd.model.DataModel
import com.squareup.picasso.Picasso


class HotelDetailsAdapter(
    private var mContext: Context,
    private var mRoom: List<DataModel>
) : RecyclerView.Adapter<HotelDetailsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HotelDetailsAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(
            R.layout.room_item_list_with_cart,
            parent,
            false
        )
        return HotelDetailsAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: HotelDetailsAdapter.ViewHolder, position: Int) {
        val model = mRoom[position]
        holder.roomNo.text = model.roomNo
        holder.desc.text = model.description
        Picasso.get().load(model.imageUrl).placeholder(R.drawable.empty).into(holder.roomImage)
        holder.visit.setOnClickListener {
            val intent = Intent(mContext, RoomDetailsActivity::class.java)
            intent.putExtra("hotelID", model.hotelID)
            intent.putExtra("hotelName", model.hotelName)
            intent.putExtra("roomKey", model.roomKey)
            intent.putExtra("imageUrl", model.imageUrl)
            intent.putExtra("roomNo", model.roomNo)
            intent.putExtra("description", model.description)
            intent.putExtra("price", model.price)
            intent.putExtra("ac", model.ac)
            intent.putExtra("capacity", model.capacity)
            mContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return mRoom.size
    }

    class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var roomImage: ImageView = itemView.findViewById(R.id.room_image)
        var roomNo: TextView = itemView.findViewById(R.id.room_no)
        var desc: TextView = itemView.findViewById(R.id.desc)
        var visit: TextView = itemView.findViewById(R.id.visit)
    }
}