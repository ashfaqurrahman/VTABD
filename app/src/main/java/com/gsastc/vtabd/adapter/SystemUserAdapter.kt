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
import com.gsastc.vtabd.model.DataModel
import com.gsastc.vtabd.utils.alertDialog

class SystemUserAdapter (
    private var mContext: Context,
    private var mUser: List<DataModel>) : RecyclerView.Adapter<SystemUserAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SystemUserAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.system_user_list_item, parent, false)
        return SystemUserAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SystemUserAdapter.ViewHolder, position: Int) {
        val model = mUser[position]
        holder.userIndex.text = (position+1).toString()
        holder.userName.text = model.name
        holder.userPhone.text = model.phone
        holder.role.text = model.role
        holder.delete.setOnClickListener {
            alertDialog(mContext, "Are you want to delete this user \nforever?")
        }
    }

    override fun getItemCount(): Int {
        return mUser.size
    }

    class ViewHolder (@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userIndex: TextView = itemView.findViewById(R.id.user_index)
        var userName: TextView = itemView.findViewById(R.id.user_name)
        var userPhone: TextView = itemView.findViewById(R.id.user_phone)
        var role: TextView = itemView.findViewById(R.id.role)
        var delete: ImageView = itemView.findViewById(R.id.delete)
    }
}