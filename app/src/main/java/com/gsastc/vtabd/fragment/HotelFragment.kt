package com.gsastc.vtabd.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.gsastc.vtabd.R
import com.gsastc.vtabd.adapter.HotelAdapter
import com.gsastc.vtabd.model.DataModel

class HotelFragment : Fragment() {
    private var recyclerView: RecyclerView? = null
    private var shimmerFrameLayout: ShimmerFrameLayout? = null
    private var placeAdapter: HotelAdapter? = null
    private var dataList: MutableList<DataModel>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_hotel, container, false)

        shimmerFrameLayout = view.findViewById(R.id.hotel_shimmer_view_container) as ShimmerFrameLayout
        recyclerView = view.findViewById(R.id.hotel_list)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        dataList = ArrayList()
        retrieveHotel()

        return view
    }

    private fun retrieveHotel() {
        val placeRef = FirebaseDatabase.getInstance().reference.child("Hotel")
        placeRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataList?.clear()
                for (snapshot in dataSnapshot.children) {
                    val hotels = snapshot.getValue(DataModel::class.java)
                    if (hotels != null) {
                        dataList?.add(hotels)

                        placeAdapter = context?.let { HotelAdapter(it, dataList as ArrayList<DataModel>) }
                        recyclerView?.adapter = placeAdapter

                        shimmerFrameLayout?.stopShimmer()
                        shimmerFrameLayout?.visibility = View.GONE
                    }
                }
                placeAdapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun onResume() {
        shimmerFrameLayout?.startShimmer()
        super.onResume()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            requireFragmentManager().beginTransaction().detach(this).attach(this).commit()
        }
    }
}
