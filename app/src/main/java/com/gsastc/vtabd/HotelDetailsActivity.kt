package com.gsastc.vtabd

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.gsastc.vtabd.adapter.HomeSliderAdapter
import com.gsastc.vtabd.adapter.HotelDetailsAdapter
import com.gsastc.vtabd.model.DataModel
import com.gsastc.vtabd.model.HomeSliderModel
import kotlinx.android.synthetic.main.activity_hotel_details.*
import kotlinx.android.synthetic.main.select_date_dialog.*
import kotlinx.android.synthetic.main.select_room_persion_dialog.*
import java.util.*
import kotlin.collections.ArrayList

class HotelDetailsActivity : AppCompatActivity() {

    private var recyclerView: RecyclerView? = null
    private var placeAdapter: HotelDetailsAdapter? = null
    private var mHotels: MutableList<DataModel>? = null

    lateinit var mAuth: FirebaseAuth
    var currentUserID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hotel_details)

        back!!.setOnClickListener { finish() }

        mAuth = FirebaseAuth.getInstance()
        currentUserID = mAuth.currentUser?.uid
        val hotelRef = FirebaseDatabase.getInstance().reference.child("Hotel")
            .child(intent.getStringExtra("hotelID").toString())
        SharedPrefManager.getInstance(this)?.saveTargetedUserID(intent.getStringExtra("hotelID"))
        val myData = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.getValue(DataModel::class.java)
                if (data?.hotelName != null) {
                    toolbar_hotel_name?.text = data.hotelName
                    hotel_name?.text = data.hotelName
                    hotel_desc?.text = data.hotelDescription
                    val movies = arrayListOf<HomeSliderModel>()
                    movies.add(HomeSliderModel(data.imageUrl))
                    movies.add(HomeSliderModel(data.imageUrl))
                    viewPager.adapter = HomeSliderAdapter(movies)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(
                    this@HotelDetailsActivity,
                    "Please give your Bio",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        hotelRef.addValueEventListener(myData)

        recyclerView = findViewById(R.id.room_list)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        mHotels = ArrayList()
        retrieveRoom()
    }

    private fun retrieveRoom() {
        val roomRef = FirebaseDatabase.getInstance().reference.child("Rooms")
        val queue = roomRef.orderByChild("hotelID").equalTo(intent.getStringExtra("hotelID"))
        queue.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mHotels?.clear()
                for (snapshot in dataSnapshot.children) {
                    val model = snapshot.getValue(DataModel::class.java)
                    if (model != null) {
                        mHotels?.add(model)
                        placeAdapter = HotelDetailsAdapter(
                            this@HotelDetailsActivity,
                            mHotels as ArrayList<DataModel>
                        )
                        recyclerView?.adapter = placeAdapter
                        empty_room?.visibility = View.GONE
                        recyclerView?.visibility = View.VISIBLE
                    }
                }
                placeAdapter?.notifyDataSetChanged()

                if (mHotels?.size == 0) {
                    empty_room?.visibility = View.VISIBLE
                    recyclerView?.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}