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

class SystemRoomAdapter(
    private var mContext: Context,
    private var mRoom: List<DataModel>
) : RecyclerView.Adapter<SystemRoomAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SystemRoomAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.system_room_item_list, parent, false)
        return SystemRoomAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SystemRoomAdapter.ViewHolder, position: Int) {
        val rooms = mRoom[position]
        val roomRef = FirebaseDatabase.getInstance().reference.child("Rooms")
        val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val currentUserID = mAuth.currentUser?.uid
        if (currentUserID == "dyQK5ePVnJVYOF85bcbEX27XUTI2") {
            holder.roomVisibility.setOnClickListener {
                if (rooms.visibility == "Show") {
                    roomRef.child(rooms.roomKey!!).child("visibility").setValue("Hide")
                }
                else {
                    roomRef.child(rooms.roomKey!!).child("visibility").setValue("Show")
                }
                holder.roomVisibility.text = rooms.visibility
            }
        }
        Picasso.get().load(rooms.imageUrl).placeholder(R.drawable.empty)
            .into(holder.roomImage)
        holder.roomNo.text = rooms.roomNo
        holder.roomCapacity.text = rooms.capacity
        holder.roomPrice.text = rooms.price
        holder.ac.text = rooms.ac
        holder.washroom.text = rooms.washRoom
        holder.roomDesc.text = rooms.description
        holder.roomVisibility.text = rooms.visibility
        holder.delete.setOnClickListener {
            alertDialog(mContext, "Are you want delete this room \nforever?", roomRef.child(rooms.roomKey!!))
        }

    }

    override fun getItemCount(): Int {
        return mRoom.size
    }

    class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var roomImage: ImageView = itemView.findViewById(R.id.room_image)
        var roomNo: TextView = itemView.findViewById(R.id.room_no)
        var roomCapacity: TextView = itemView.findViewById(R.id.room_capacity)
        var roomPrice: TextView = itemView.findViewById(R.id.room_price)
        var ac: TextView = itemView.findViewById(R.id.ac)
        var washroom: TextView = itemView.findViewById(R.id.washroom)
        var roomDesc: TextView = itemView.findViewById(R.id.room_desc)
        var roomVisibility: TextView = itemView.findViewById(R.id.room_visibility)
        var delete: ImageView = itemView.findViewById(R.id.delete)
    }
}