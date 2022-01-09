package com.gsastc.vtabd.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gsastc.vtabd.R
import com.gsastc.vtabd.adapter.PlaceAdapter
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.gsastc.vtabd.model.DataModel


@Suppress("DEPRECATION")
open class PlaceFragment : Fragment() {
    private var recyclerView: RecyclerView? = null
    private var shimmerFrameLayout: ShimmerFrameLayout? = null
    private var placeAdapter: PlaceAdapter? = null
    private var mPlaces: MutableList<DataModel>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_place, container, false)

        shimmerFrameLayout = view.findViewById(R.id.place_shimmer_view_container) as ShimmerFrameLayout

        recyclerView = view.findViewById(R.id.place_list)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)

        mPlaces = ArrayList()

        retrievePlaces()
        
        return view
    }

    private fun retrievePlaces() {
        val placeRef = FirebaseDatabase.getInstance().reference.child("Place")
        placeRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mPlaces?.clear()
                for (snapshot in dataSnapshot.children) {
                    val places = snapshot.getValue(DataModel::class.java)
                    if (places != null) {
                        mPlaces?.add(places)
                        placeAdapter = context?.let { PlaceAdapter(it, mPlaces as ArrayList<DataModel>) }
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
