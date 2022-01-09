package com.gsastc.vtabd

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.gsastc.vtabd.adapter.SystemVehicleAdapter
import com.gsastc.vtabd.model.DataModel
import kotlinx.android.synthetic.main.activity_system_vehicles.*
import java.util.*
import kotlin.collections.ArrayList

class SystemVehiclesActivity : AppCompatActivity() {
    private var recyclerView: RecyclerView? = null
    private var shimmerFrameLayout: ShimmerFrameLayout? = null
    private var privateTransportAdapter: SystemVehicleAdapter? = null
    private var transportDataList: MutableList<DataModel>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_system_vehicles)

        val mToolbar = findViewById<MaterialToolbar>(R.id.vehicles_toolbar)
        setSupportActionBar(mToolbar)
        Objects.requireNonNull(supportActionBar)?.setDisplayHomeAsUpEnabled(true)
        val toolbarTv = findViewById<TextView>(R.id.vehicles_tv)
        toolbarTv.text = "Vehicles"

        shimmerFrameLayout = findViewById(R.id.vehicles_shimmer_view_container)
        recyclerView = findViewById(R.id.vehicles_list)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        transportDataList = ArrayList()
        retrieveHotel()
    }

    private fun retrieveHotel() {
        val hotelRef = FirebaseDatabase.getInstance().reference.child("vehicle")
        hotelRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                transportDataList?.clear()
                for (snapshot in dataSnapshot.children) {
                    val model = snapshot.getValue(DataModel::class.java)
                    if (model != null) {
                        transportDataList?.add(model)
                        privateTransportAdapter = SystemVehicleAdapter(this@SystemVehiclesActivity, transportDataList as ArrayList<DataModel>)
                        recyclerView?.adapter = privateTransportAdapter
                        shimmerFrameLayout?.stopShimmer()
                        shimmerFrameLayout?.visibility = View.GONE
                        vehicle_layout?.visibility = View.VISIBLE
                        empty_vehicles?.visibility = View.GONE
                    }
                }
                privateTransportAdapter?.notifyDataSetChanged()

                if (transportDataList?.size == 0) {
                    shimmerFrameLayout?.stopShimmer()
                    shimmerFrameLayout?.visibility = View.GONE
                    empty_vehicles?.visibility = View.VISIBLE
                    vehicle_layout?.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

}