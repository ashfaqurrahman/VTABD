package com.gsastc.vtabd

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.database.*
import com.gsastc.vtabd.adapter.PlaceAdapter
import com.gsastc.vtabd.model.DataModel
import kotlinx.android.synthetic.main.activity_search_result_place_list.*

class SearchResultPlaceListActivity : AppCompatActivity() {
    private var recyclerView: RecyclerView? = null
    private var shimmerFrameLayout: ShimmerFrameLayout? = null
    private var placeAdapter: PlaceAdapter? = null
    private var mPlaces: MutableList<DataModel>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result_place_list)

        back_city_list?.setOnClickListener {
            finish()
        }

        toolbar_search_result_place_list?.text = "Places of " + intent.getStringExtra("city")

        shimmerFrameLayout = findViewById(R.id.place_list_shimmer_view_container)

        recyclerView = findViewById(R.id.place_list)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(this)

        mPlaces = ArrayList()

        retrievePlaces(intent.getStringExtra("city"))
    }

    private fun retrievePlaces(city:String?) {
        val placeRef = FirebaseDatabase.getInstance().reference.child("Place")
        val query: Query = placeRef.orderByChild("city").equalTo(city)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mPlaces?.clear()
                for (snapshot in dataSnapshot.children) {
                    val places = snapshot.getValue(DataModel::class.java)
                    if (places != null) {
                        mPlaces?.add(places)

                        placeAdapter = PlaceAdapter(this@SearchResultPlaceListActivity, mPlaces as ArrayList<DataModel>)
                        recyclerView?.adapter = placeAdapter

                        shimmerFrameLayout?.stopShimmer()
                        shimmerFrameLayout?.visibility = View.GONE
                        empty_place?.visibility = View.GONE
                    }
                }
                placeAdapter?.notifyDataSetChanged()

                if (mPlaces?.size == 0) {
                    shimmerFrameLayout?.stopShimmer()
                    shimmerFrameLayout?.visibility = View.GONE
                    empty_place?.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}