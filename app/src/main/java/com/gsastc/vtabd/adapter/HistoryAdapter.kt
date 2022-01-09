package com.gsastc.vtabd.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.gsastc.vtabd.R
import com.gsastc.vtabd.model.DataModel

class HistoryAdapter (
    private var mContext: Context,
    private var mGuides: List<DataModel>) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.history_item, parent, false)
        return HistoryAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryAdapter.ViewHolder, position: Int) {
        val model = mGuides[position]

        holder.name.text = model.name
        holder.type.text = model.type
        holder.checkInCheckOut.text = model.checkInCheckOut + " (" + model.days + " day)"
        holder.rantPerDay.text = model.rantPerDay
        holder.totalAmount.text = model.totalAmount
        holder.status.text = model.status
        if (model.status == "Pending") {
            holder.status.setTextColor(ContextCompat.getColor(mContext, R.color.md_yellow_A700));
        }
        if (model.status == "Accepted") {
            holder.status.setTextColor(ContextCompat.getColor(mContext, R.color.md_light_green_A700));
        }
        if (model.status == "Rejected") {
            holder.status.setTextColor(ContextCompat.getColor(mContext, R.color.md_red_A700));
        }
    }

    override fun getItemCount(): Int {
        return mGuides.size
    }

    class ViewHolder (@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView = itemView.findViewById(R.id.name)
        var type: TextView = itemView.findViewById(R.id.type)
        var checkInCheckOut: TextView = itemView.findViewById(R.id.check_in_check_out)
        var rantPerDay: TextView = itemView.findViewById(R.id.rent_per_day)
        var totalAmount: TextView = itemView.findViewById(R.id.total_amount)
        var status: TextView = itemView.findViewById(R.id.status)
    }
}