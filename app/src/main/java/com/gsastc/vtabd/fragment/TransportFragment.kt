package com.gsastc.vtabd.fragment

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.archit.calendardaterangepicker.customviews.DateRangeCalendarView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import com.gsastc.vtabd.R
import com.gsastc.vtabd.adapter.TransportAdapter
import com.gsastc.vtabd.model.DataModel
import kotlinx.android.synthetic.main.fragment_transport.*
import kotlin.collections.ArrayList

class TransportFragment : Fragment() {
    private var recyclerView: RecyclerView? = null
    private var shimmerFrameLayout: ShimmerFrameLayout? = null
    private var privateTransportAdapter: TransportAdapter? = null
    private var privateTransportModel: MutableList<DataModel>? = null
    private var calendar: DateRangeCalendarView? = null
    private var dateRangeDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_transport, container, false)

        shimmerFrameLayout = view.findViewById(R.id.transport_shimmer_view_container) as ShimmerFrameLayout
        recyclerView = view.findViewById(R.id.transport_list)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        privateTransportModel = ArrayList()
        retrieveTransport()
        return view
    }

    private fun retrieveTransport() {
        val placeRef = FirebaseDatabase.getInstance().reference.child("vehicle")
        placeRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                privateTransportModel?.clear()
                for (snapshot in dataSnapshot.children) {
                    val transports = snapshot.getValue(DataModel::class.java)
                    if (transports != null) {
                        privateTransportModel?.add(transports)
                        privateTransportAdapter = context?.let { TransportAdapter(it, privateTransportModel as ArrayList<DataModel>) }
                        recyclerView?.adapter = privateTransportAdapter
                        shimmerFrameLayout?.stopShimmer()
                        shimmerFrameLayout?.visibility = View.GONE
                        recyclerView?.visibility = View.VISIBLE
                        empty_transport?.visibility = View.GONE
                    }
                }
                privateTransportAdapter?.notifyDataSetChanged()
                if (privateTransportModel?.size == 0) {
                    shimmerFrameLayout?.stopShimmer()
                    shimmerFrameLayout?.visibility = View.GONE
                    recyclerView?.visibility = View.GONE
                    empty_transport?.visibility = View.VISIBLE
                }
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
