package com.gsastc.vtabd.fragment

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.gsastc.vtabd.PhoneActivity
import com.gsastc.vtabd.R
import com.gsastc.vtabd.SharedPrefManager
import com.gsastc.vtabd.model.DataModel
import com.gsastc.vtabd.utils.logoutUser
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_guide_profile.view.*

class GuideProfileFragment : Fragment() {

    private var mAuth: FirebaseAuth? = null
    private var guideRef: DatabaseReference? = null
    private var currentUserID: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_guide_profile, container, false)

        requireActivity().bottom_navigation.menu.getItem(1).isChecked = true

        val loading = ProgressDialog.show(
            context,
            "",
            "Please wait...",
            false,
            false
        )
        loading?.show()

        mAuth = FirebaseAuth.getInstance()
        currentUserID = mAuth?.currentUser?.uid
        guideRef = FirebaseDatabase.getInstance().reference.child("guide").child(currentUserID!!)

        val myData = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.getValue(DataModel::class.java)
                if (data?.guideName != null) {
                    Picasso.get().load(data.imageUrl).placeholder(R.drawable.empty)
                        .into(view?.profile_image)
                    view?.guide_name?.text = data.guideName
                    view?.guide_phone?.text = data.guidePhone
                    view?.guide_email?.text = data.guideEmail
                    view?.guide_address?.text = data.cityName + ", " + data.division
                    loading?.dismiss()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(
                    context,
                    "Please give your Bio",
                    Toast.LENGTH_LONG
                ).show()
                loading?.dismiss()
            }
        }
        guideRef!!.addValueEventListener(myData)

        view?.logout?.setOnClickListener {
            logoutUser(requireContext())
        }

        return view
    }

}