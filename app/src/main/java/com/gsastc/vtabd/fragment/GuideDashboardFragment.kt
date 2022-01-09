package com.gsastc.vtabd.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.gsastc.vtabd.R
import com.gsastc.vtabd.adapter.ReservationsAdapter
import com.gsastc.vtabd.model.DataModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_guide_dashboard.*
import java.util.ArrayList

class GuideDashboardFragment : Fragment() {

    private var recyclerView: RecyclerView? = null
    private var shimmerFrameLayout: ShimmerFrameLayout? = null
    private var reservationAdapter: ReservationsAdapter? = null
    private var dataList: MutableList<DataModel>? = null
    private lateinit var mAuth: FirebaseAuth
    private var currentUserID: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_guide_dashboard, container, false)

        requireActivity().bottom_navigation.menu.getItem(0).isChecked = true

        val typeface = ResourcesCompat.getFont(requireContext(), R.font.blacklist)
        val appName = view.findViewById<TextView>(R.id.appname)
        appName.typeface = typeface

        mAuth = FirebaseAuth.getInstance()
        currentUserID = mAuth.currentUser?.uid
        shimmerFrameLayout = view.findViewById(R.id.guide_shimmer_view_container)
        recyclerView = view.findViewById(R.id.guide_order_list)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        dataList = ArrayList()
        retrieveReservation()

        return view
    }

    private fun retrieveReservation() {
        val orderRef = FirebaseDatabase.getInstance().reference.child("guide").child(currentUserID!!).child("reservations")
        orderRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataList?.clear()
                for (snapshot in dataSnapshot.children) {
                    val orders = snapshot.getValue(DataModel::class.java)
                    if (orders != null) {
                        dataList?.add(orders)
                        reservationAdapter = ReservationsAdapter(context!!, dataList as ArrayList<DataModel>)
                        recyclerView?.adapter = reservationAdapter
                        shimmerFrameLayout?.stopShimmer()
                        shimmerFrameLayout?.visibility = View.GONE
                        recyclerView?.visibility = View.VISIBLE
                        empty_guide_order?.visibility = View.GONE
                    }
                }
                reservationAdapter?.notifyDataSetChanged()

                if (dataList?.size == 0) {
                    shimmerFrameLayout?.stopShimmer()
                    empty_guide_order?.visibility = View.VISIBLE
                    recyclerView?.visibility = View.GONE
                    shimmerFrameLayout?.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}