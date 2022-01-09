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

class AcReOrderAdapter (
    private var mContext: Context,
    private var mGuides: List<DataModel>) : RecyclerView.Adapter<AcReOrderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AcReOrderAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.system_order_item_list, parent, false)
        return AcReOrderAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: AcReOrderAdapter.ViewHolder, position: Int) {
        val model = mGuides[position]

        holder.details.text = model.name
        holder.type.text = model.type
        holder.checkInCheckOut.text = model.checkInCheckOut
        holder.quantity.text = model.days
        holder.rantPerDay.text = model.rantPerDay
        holder.totalAmount.text = model.totalAmount
        holder.status.text = model.status
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
        var details: TextView = itemView.findViewById(R.id.details)
        var type: TextView = itemView.findViewById(R.id.type)
        var checkInCheckOut: TextView = itemView.findViewById(R.id.check_in_check_out)
        var quantity: TextView = itemView.findViewById(R.id.quantity)
        var rantPerDay: TextView = itemView.findViewById(R.id.unit_price)
        var totalAmount: TextView = itemView.findViewById(R.id.total_price)
        var status: TextView = itemView.findViewById(R.id.status)
    }
}