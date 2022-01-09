package com.gsastc.vtabd.fragment

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.gsastc.vtabd.R
import com.gsastc.vtabd.model.AuthorModel
import com.gsastc.vtabd.utils.logoutUser
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_author_profile.view.*

class AuthorProfileFragment : Fragment() {

    private var mAuth: FirebaseAuth? = null
    private var guideRef: DatabaseReference? = null
    private var currentUserID: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_author_profile, container, false)

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
        guideRef = FirebaseDatabase.getInstance().reference.child("author").child(currentUserID!!)
        val myData = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.getValue(AuthorModel::class.java)
                if (data?.authorName != null) {
                    Picasso.get().load(data.imageUrl).placeholder(R.drawable.empty)
                        .into(view?.profile_image)
                    view?.author_name?.text = data.authorName
                    view?.author_phone?.text = data.authorPhone
                    view?.author_email?.text = data.authorEmail
                    view?.author_address?.text = data.cityName + ", " + data.division
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