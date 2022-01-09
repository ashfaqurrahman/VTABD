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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.gsastc.vtabd.BlogDetailsActivity
import com.gsastc.vtabd.R
import com.gsastc.vtabd.model.DataModel
import com.gsastc.vtabd.utils.alertDialog
import com.squareup.picasso.Picasso


class SystemBlogAdapter(
    private var mContext: Context,
    private var dataModel: List<DataModel>
) : RecyclerView.Adapter<SystemBlogAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SystemBlogAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.system_blog_list_item, parent, false)
        return SystemBlogAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SystemBlogAdapter.ViewHolder, position: Int) {
        val model = dataModel[position]
        val ref = FirebaseDatabase.getInstance().reference.child("blog").child(model.blogKey!!)
        Picasso.get().load(model.imageUrl).placeholder(R.drawable.empty)
            .into(holder.blogImage)
        holder.blogTitle.text = model.blogName
        holder.blogAuthor.text = model.authorName
        holder.blogDesc.text = model.description
        holder.roomVisibility.text = model.visibility
        val mAuth = FirebaseAuth.getInstance()
        val currentUserID = mAuth.currentUser?.uid
        if (currentUserID == "dyQK5ePVnJVYOF85bcbEX27XUTI2") {
            holder.roomVisibility.setOnClickListener {
                if (model.visibility == "Show") {
                    ref.child("visibility").setValue("Hide")
                }
                else {
                    ref.child("visibility").setValue("Show")
                }
                holder.roomVisibility.text = model.visibility
            }
        }
        holder.delete.setOnClickListener {
            alertDialog(mContext, "Are you want to delete this blog \nforever?", ref)
        }
        holder.parentLayout.setOnClickListener {
            val intent = Intent(mContext, BlogDetailsActivity::class.java)
            intent.putExtra("date", model.date)
            intent.putExtra("time", model.time)
            intent.putExtra("imageUrl", model.imageUrl)
            intent.putExtra("authorName", model.authorName)
            intent.putExtra("blogName", model.blogName)
            intent.putExtra("description", model.description)
            mContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return dataModel.size
    }

    class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var blogImage: ImageView = itemView.findViewById(R.id.blog_image)
        var blogTitle: TextView = itemView.findViewById(R.id.blog_title)
        var blogAuthor: TextView = itemView.findViewById(R.id.blog_author)
        var blogDesc: TextView = itemView.findViewById(R.id.blog_desc)
        var roomVisibility: TextView = itemView.findViewById(R.id.room_visibility)
        var parentLayout: LinearLayout = itemView.findViewById(R.id.parent_layout)
        var delete: ImageView = itemView.findViewById(R.id.delete)
    }
}