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
import com.gsastc.vtabd.adapter.PlaceAdapter
import com.gsastc.vtabd.model.DataModel
import kotlinx.android.synthetic.main.activity_system_place.*
import java.util.*
import kotlin.collections.ArrayList

class SystemPlaceActivity : AppCompatActivity() {
    private var recyclerView: RecyclerView? = null
    private var shimmerFrameLayout: ShimmerFrameLayout? = null
    private var placeAdapter: PlaceAdapter? = null
    private var placeDataList: MutableList<DataModel>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_system_place)

        val mToolbar = findViewById<MaterialToolbar>(R.id.place_toolbar)
        setSupportActionBar(mToolbar)
        Objects.requireNonNull(supportActionBar)?.setDisplayHomeAsUpEnabled(true)
        val toolbarTv = findViewById<TextView>(R.id.place_tv)
        toolbarTv.text = "Locations"

        shimmerFrameLayout = findViewById(R.id.place_shimmer_view_container)
        recyclerView = findViewById(R.id.place_list)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        placeDataList = ArrayList()
        retrievePlace()
    }

    private fun retrievePlace() {
        val placeRef = FirebaseDatabase.getInstance().reference.child("Place")
        placeRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                placeDataList?.clear()
                for (snapshot in dataSnapshot.children) {
                    val model = snapshot.getValue(DataModel::class.java)
                    if (model != null) {
                        placeDataList?.add(model)
                        placeAdapter = PlaceAdapter(this@SystemPlaceActivity, placeDataList as ArrayList<DataModel>)
                        recyclerView?.adapter = placeAdapter
                        shimmerFrameLayout?.stopShimmer()
                        shimmerFrameLayout?.visibility = View.GONE
                        recyclerView?.visibility = View.VISIBLE
                        empty_place?.visibility = View.GONE
                    }
                }
                placeAdapter?.notifyDataSetChanged()

                if (placeDataList?.size == 0) {
                    shimmerFrameLayout?.stopShimmer()
                    empty_place?.visibility = View.VISIBLE
                    recyclerView?.visibility = View.GONE
                    shimmerFrameLayout?.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}