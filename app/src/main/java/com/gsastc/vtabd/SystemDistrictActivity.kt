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
import com.gsastc.vtabd.adapter.SystemDistrictAdapter
import com.gsastc.vtabd.model.DataModel
import kotlinx.android.synthetic.main.activity_system_district.*
import java.util.*
import kotlin.collections.ArrayList

class SystemDistrictActivity : AppCompatActivity() {
    private var recyclerView: RecyclerView? = null
    private var shimmerFrameLayout: ShimmerFrameLayout? = null
    private var districtAdapter: SystemDistrictAdapter? = null
    private var cityDataList: MutableList<DataModel>? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_system_district)

        val mToolbar = findViewById<MaterialToolbar>(R.id.city_toolbar)
        setSupportActionBar(mToolbar)
        Objects.requireNonNull(supportActionBar)?.setDisplayHomeAsUpEnabled(true)
        val toolbarTv = findViewById<TextView>(R.id.city_tv)
        toolbarTv.text = "Districts"

        shimmerFrameLayout = findViewById(R.id.city_shimmer_view_container)
        recyclerView = findViewById(R.id.city_list)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        cityDataList = ArrayList()
        retrieveCity()
    }

    private fun retrieveCity() {
        val hotelRef = FirebaseDatabase.getInstance().reference.child("district")
        hotelRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                cityDataList?.clear()
                for (snapshot in dataSnapshot.children) {
                    val model = snapshot.getValue(DataModel::class.java)
                    if (model != null) {
                        cityDataList?.add(model)
                        districtAdapter = SystemDistrictAdapter(this@SystemDistrictActivity, cityDataList as ArrayList<DataModel>)
                        recyclerView?.adapter = districtAdapter
                        shimmerFrameLayout?.stopShimmer()
                        shimmerFrameLayout?.visibility = View.GONE
                        district_layout?.visibility = View.VISIBLE
                        empty_city?.visibility = View.GONE
                    }
                }
                districtAdapter?.notifyDataSetChanged()

                if (cityDataList?.size == 0) {
                    shimmerFrameLayout?.stopShimmer()
                    empty_city?.visibility = View.VISIBLE
                    district_layout?.visibility = View.GONE
                    shimmerFrameLayout?.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}