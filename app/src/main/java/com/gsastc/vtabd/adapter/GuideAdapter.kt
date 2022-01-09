package com.gsastc.vtabd.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.gsastc.vtabd.GuideDetailsActivity
import com.gsastc.vtabd.R
import com.gsastc.vtabd.RoomDetailsActivity
import com.gsastc.vtabd.model.DataModel
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class GuideAdapter(
    private var mContext: Context,
    private var mGuides: List<DataModel>
) : RecyclerView.Adapter<GuideAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuideAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.guide_list_item, parent, false)
        return GuideAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: GuideAdapter.ViewHolder, position: Int) {
        val model = mGuides[position]
        holder.guideName.text = model.guideName
        holder.district.text = model.cityName
        holder.rant.text = "BDT " + model.guideRant
        Picasso.get().load(model.imageUrl).placeholder(R.drawable.empty)
            .into(holder.guideImage)
        holder.parentLayout.setOnClickListener {
            val intent = Intent(mContext, GuideDetailsActivity::class.java)
            intent.putExtra("uid", model.uid)
            intent.putExtra("imageUrl", model.imageUrl)
            intent.putExtra("guideName", model.guideName)
            intent.putExtra("cityName", model.cityName)
            intent.putExtra("guideRant", model.guideRant)
            mContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return mGuides.size
    }

    class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var parentLayout: RelativeLayout = itemView.findViewById(R.id.parent_layout)
        var guideName: TextView = itemView.findViewById(R.id.guide_name)
        var district: TextView = itemView.findViewById(R.id.guide_district)
        var rant: TextView = itemView.findViewById(R.id.rant)
        var guideImage: CircleImageView = itemView.findViewById(R.id.guide_image)
    }
}