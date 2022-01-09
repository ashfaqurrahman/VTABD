package com.gsastc.vtabd.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.gsastc.vtabd.BlogDetailsActivity
import com.gsastc.vtabd.R
import com.gsastc.vtabd.model.DataModel
import com.squareup.picasso.Picasso


class BlogAdapter(
    private var mContext: Context,
    private var dataModel: List<DataModel>
) : RecyclerView.Adapter<BlogAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.blog_list_item, parent, false)
        return BlogAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: BlogAdapter.ViewHolder, position: Int) {
        val model = dataModel[position]

        holder.date.text = model.date
        holder.time.text = model.time
        holder.blogTitle.text = model.blogName
        holder.blogDesc.text = model.description
        Picasso.get().load(model.imageUrl).placeholder(R.drawable.empty)
            .into(holder.blogImage)
        holder.parentLayout.setOnClickListener {
            val intent = Intent(mContext, BlogDetailsActivity::class.java)
            intent.putExtra("date", model.date)
            intent.putExtra("time", model.time)
            intent.putExtra("blogName", model.blogName)
            intent.putExtra("authorName", model.authorName)
            intent.putExtra("description", model.description)
            intent.putExtra("imageUrl", model.imageUrl)
            mContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return dataModel.size
    }

    class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var date: TextView = itemView.findViewById(R.id.date)
        var time: TextView = itemView.findViewById(R.id.time)
        var blogImage: ImageView = itemView.findViewById(R.id.blog_image)
        var blogTitle: TextView = itemView.findViewById(R.id.blog_title)
        var blogDesc: TextView = itemView.findViewById(R.id.blog_desc)
        var parentLayout: LinearLayout = itemView.findViewById(R.id.parent_layout)
    }
}