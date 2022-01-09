package com.gsastc.vtabd.adapter

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.gsastc.vtabd.R
import com.gsastc.vtabd.model.DataModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import java.util.*


class GalleryAdapter(
    private var mContext: Context,
    private var mGallery: List<DataModel>
) : RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.gallery_item_list, parent, false)
        return GalleryAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: GalleryAdapter.ViewHolder, position: Int) {
        val model = mGallery[position]

        Picasso.get().load(model.imageUrl).placeholder(R.drawable.empty)
            .into(holder.galleryImage)
        holder.galleryImage.setOnClickListener {
            var currentUserID: String? = null
            val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
            currentUserID = mAuth.currentUser?.uid
            val roomRef = FirebaseDatabase.getInstance().reference.child("Hotel").child(
                currentUserID!!
            ).child("gallery")

            val builder = Dialog(mContext)
            builder.setContentView(R.layout.dialog_delete_room)

            val no = builder.findViewById<TextView>(R.id.cancel)
            val yes = builder.findViewById<TextView>(R.id.yes)

            no.setOnClickListener {
                builder.dismiss()
            }

            yes.setOnClickListener {
                Toast.makeText(
                    mContext.applicationContext,
                    "Image Delete Successfully",
                    Toast.LENGTH_SHORT
                ).show()
                roomRef.child(model.galleryKey!!).removeValue()
                builder.dismiss()
            }

            Objects.requireNonNull(builder.window)
                ?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            builder.window
                ?.setLayout(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

            builder.show()
        }
    }

    override fun getItemCount(): Int {
        return mGallery.size
    }

    class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var galleryImage: ImageView = itemView.findViewById(R.id.gallery_image)
    }
}