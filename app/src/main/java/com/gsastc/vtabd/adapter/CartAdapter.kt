package com.gsastc.vtabd.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.gsastc.vtabd.R
import com.gsastc.vtabd.model.CartModel
import java.util.*

class CartAdapter(private val context: Context, private val itemList: ArrayList<CartModel>) :
    RecyclerView.Adapter<CartAdapter.ViewHolder>() {
    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myDB: DatabaseHelper? = null
        val model = itemList[position]
        holder.name.text = model.name
        holder.type.text = model.type
        holder.checkInCheckOut.text = model.checkInCheckOut + " (" + model.days + " days)"
        holder.rantPerDay.text = model.rantPerDay
        holder.totalAmount.text = model.totalAmount
        holder.delete.visibility = View.GONE
        holder.delete.setOnClickListener {
            model.id?.let { it1 -> myDB?.removeItem(it1) }
            Toast.makeText(
                context, "Item removed " + model.id , Toast.LENGTH_SHORT
            ).show()
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView = itemView.findViewById(R.id.name)
        var type: TextView = itemView.findViewById(R.id.type)
        var checkInCheckOut: TextView = itemView.findViewById(R.id.check_in_check_out)
        var rantPerDay: TextView = itemView.findViewById(R.id.rent_per_day)
        var totalAmount: TextView = itemView.findViewById(R.id.total_amount)
        var delete: ImageView = itemView.findViewById(R.id.delete)

    }
}