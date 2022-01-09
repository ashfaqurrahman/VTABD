package com.gsastc.vtabd.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.gsastc.vtabd.R
import com.gsastc.vtabd.SharedPrefManager
import com.gsastc.vtabd.adapter.TabAdapter
import com.gsastc.vtabd.model.DataModel
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.gsastc.vtabd.SearchActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.view.*

@Suppress("DEPRECATION")
class HomeFragment : Fragment() {

    private var mAuth: FirebaseAuth? = null
    private var userRef: DatabaseReference? = null
    private var currentUserID: String? = null
    var viewPager: ViewPager? = null
    private var tabLayout: TabLayout? = null
    private var downloadImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)


        // show location that save on sharedPreferences
        val location =
            SharedPrefManager.getInstance(requireContext())!!.getDistrict + ", " + SharedPrefManager.getInstance(
                requireContext()
            )!!.getDivision
        if (location != null) {
            view?.home_current_location?.text = location
        } else {
            view?.home_current_location?.text = "Unknown Address"
        }

        view?.search?.setOnClickListener {
            startActivity(Intent(context, SearchActivity::class.java))
        }

        mAuth = FirebaseAuth.getInstance()
        currentUserID = mAuth?.currentUser?.uid
        userRef = FirebaseDatabase.getInstance().reference.child("users")

        // get name and profile pic for home page
        if (SharedPrefManager.getInstance(requireContext())!!.getUserName == null || SharedPrefManager.getInstance(
                requireContext()
            )!!.getUserPic == null
        ) {
            val myData = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val data = dataSnapshot.getValue(DataModel::class.java)
                    if (data?.userName != null && data?.imageUri != null) {
                        SharedPrefManager.getInstance(context!!)!!.saveUserNamePic(data?.userName.toString(), data?.imageUri.toString())
                        view?.name?.text = SharedPrefManager.getInstance(context!!)!!.getUserName
                        Picasso.get().load(SharedPrefManager.getInstance(context!!)!!.getUserPic)
                            .placeholder(R.drawable.ic_empty_profile).into(view.profile_pic)
                        downloadImageUri = Uri.parse(data?.imageUri)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(
                        context,
                        "Please give your Bio",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            userRef?.child(currentUserID.toString())?.addValueEventListener(myData)
        } else {
            view?.name?.text = SharedPrefManager.getInstance(requireContext())!!.getUserName
            Picasso.get().load(SharedPrefManager.getInstance(requireContext())!!.getUserPic)
                .placeholder(R.drawable.ic_empty_profile).into(view.profile_pic)
        }

        requireActivity().bottom_navigation.menu.getItem(0).isChecked = true

        tabLayout = view.findViewById(R.id.tabs) as TabLayout
        viewPager = view.findViewById(R.id.pager) as ViewPager
        val adapter = TabAdapter(requireFragmentManager(), tabLayout!!.tabCount)
        viewPager!!.adapter = adapter
        viewPager!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager!!.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
        return view
    }
}
