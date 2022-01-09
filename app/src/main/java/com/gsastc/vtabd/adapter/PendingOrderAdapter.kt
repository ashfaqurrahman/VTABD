package com.gsastc.vtabd.adapter

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.gsastc.vtabd.R
import com.gsastc.vtabd.model.DataModel
import com.gsastc.vtabd.utils.toasty
import java.util.HashMap

class PendingOrderAdapter (
    private var mContext: Context,
    private var mGuides: List<DataModel>) : RecyclerView.Adapter<PendingOrderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingOrderAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.system_order_item_list, parent, false)
        return PendingOrderAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: PendingOrderAdapter.ViewHolder, position: Int) {
        val model = mGuides[position]

        holder.details.text = model.name
        holder.type.text = model.type
        holder.checkInCheckOut.text = model.checkInCheckOut
        holder.quantity.text = model.days
        holder.unitPrice.text = model.rantPerDay
        holder.totalAmount.text = model.totalAmount
        holder.status.text = model.status
        holder.status.setTextColor(ContextCompat.getColor(mContext, R.color.md_yellow_A700))
        holder.status.setOnClickListener {
            statusDialog(mContext, model)
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
        var unitPrice: TextView = itemView.findViewById(R.id.unit_price)
        var totalAmount: TextView = itemView.findViewById(R.id.total_price)
        var status: TextView = itemView.findViewById(R.id.status)
    }

    private fun statusDialog(context: Context, model: DataModel) {
        val dialog = Dialog(context)
        val userName: String? = model.userName
        val userPhone: String? = model.phone
        val address: String? = model.address
        val days: String? = model.days
        val checkInCheckOut: String? = model.checkInCheckOut
        val hotelRef = FirebaseDatabase.getInstance().reference.child("Hotel")
        val guideRef = FirebaseDatabase.getInstance().reference.child("guide")
        val riderRef = FirebaseDatabase.getInstance().reference.child("transport")
        val orderRef = FirebaseDatabase.getInstance().reference.child("Order").child(model.orderKey!!)
        val historyRef = FirebaseDatabase.getInstance().reference.child("users").child(model.currentUserID!!).child("history").child(model.orderKey!!)
        dialog.setContentView(R.layout.status_dialog)
        val accepted = dialog.findViewById<TextView>(R.id.accepted)
        val rejected = dialog.findViewById<TextView>(R.id.rejected)
        accepted.setOnClickListener {
            val loading = ProgressDialog.show(
                mContext,
                "",
                "Please wait...",
                false,
                false
            )
            loading?.show()
            orderRef.child("status").setValue("Accepted")
            orderRef.child("slag").setValue("AcceptedRejected")
            historyRef.child("status").setValue("Accepted")
            if (model.type == "Hotel") {
                val data = HashMap<String, Any>()
                data["userName"] = userName.toString()
                data["phone"] = userPhone.toString()
                data["address"] = address.toString()
                data["checkInCheckOut"] = checkInCheckOut.toString()
                data["days"] = days.toString()
                data["currentUserID"] = model.currentUserID.toString()
                hotelRef.child(model.targetedUserID!!).child("reservations").push().setValue(data).addOnSuccessListener {
                    toasty(mContext, "Accepted")
                    loading.dismiss()
                }.addOnFailureListener {
                    toasty(mContext, "Error saving to DB")
                    loading.dismiss()
                }
            }
            if (model.type == "Guide") {
                val data = HashMap<String, Any>()
                data["userName"] = userName.toString()
                data["phone"] = userPhone.toString()
                data["address"] = address.toString()
                data["checkInCheckOut"] = checkInCheckOut.toString()
                data["days"] = days.toString()
                data["currentUserID"] = model.currentUserID.toString()
                guideRef.child(model.targetedUserID!!).child("reservations").push().setValue(data).addOnSuccessListener {
                    toasty(mContext, "Accepted")
                    loading.dismiss()
                }.addOnFailureListener {
                    toasty(mContext, "Error saving to DB")
                    loading.dismiss()
                }
            }
            if (model.type == "Vehicle") {
                val data = HashMap<String, Any>()
                data["userName"] = userName.toString()
                data["phone"] = userPhone.toString()
                data["address"] = address.toString()
                data["checkInCheckOut"] = checkInCheckOut.toString()
                data["days"] = days.toString()
                data["currentUserID"] = model.currentUserID.toString()
                riderRef.child(model.targetedUserID!!).child("reservations").push().setValue(data).addOnSuccessListener {
                    toasty(mContext, "Accepted")
                    loading.dismiss()
                }.addOnFailureListener {
                    toasty(mContext, "Error saving to DB")
                    loading.dismiss()
                }
            }
            dialog.dismiss()
        }

        rejected.setOnClickListener {
            orderRef.child("status").setValue("Rejected")
            orderRef.child("slag").setValue("AcceptedRejected")
            historyRef.child("status").setValue("Rejected")
            toasty(mContext, "Rejected")
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.setCancelable(true)
        dialog.show()
    }
}