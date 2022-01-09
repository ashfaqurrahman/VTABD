package com.gsastc.vtabd.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.gsastc.vtabd.adapter.HistoryAdapter
import com.gsastc.vtabd.model.DataModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_history.*


@Suppress("DEPRECATION")
class HistoryFragment : Fragment() {
    private var recyclerView: RecyclerView? = null
    private var shimmerFrameLayout: ShimmerFrameLayout? = null
    private var placeAdapter: HistoryAdapter? = null
    private var dataList: MutableList<DataModel>? = null

    private lateinit var mAuth: FirebaseAuth
    private var currentUserID: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        mAuth = FirebaseAuth.getInstance()
        currentUserID = mAuth.currentUser?.uid
        requireActivity().bottom_navigation.menu.getItem(2).isChecked = true

        shimmerFrameLayout = view.findViewById(R.id.history_shimmer_view_container) as ShimmerFrameLayout
        recyclerView = view.findViewById(R.id.history_item_list)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        dataList = ArrayList()
        retrieveHistory()

        return view
    }

    private fun retrieveHistory() {
        val historyRef = FirebaseDatabase.getInstance().reference.child("users").child(currentUserID!!).child("history")
        historyRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataList?.clear()
                for (snapshot in dataSnapshot.children) {
                    val dataModel = snapshot.getValue(DataModel::class.java)
                    if (dataModel != null) {
                        dataList?.add(dataModel)

                        placeAdapter = context?.let { HistoryAdapter(it, dataList as ArrayList<DataModel>) }
                        recyclerView?.adapter = placeAdapter

                        shimmerFrameLayout?.stopShimmer()
                        empty_history?.visibility = View.GONE
                        recyclerView?.visibility = View.VISIBLE
                        shimmerFrameLayout?.visibility = View.GONE
                    }
                }
                placeAdapter?.notifyDataSetChanged()

                if (dataList?.size == 0) {
                    shimmerFrameLayout?.stopShimmer()
                    empty_history?.visibility = View.VISIBLE
                    recyclerView?.visibility = View.GONE
                    shimmerFrameLayout?.visibility = View.GONE
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

}
