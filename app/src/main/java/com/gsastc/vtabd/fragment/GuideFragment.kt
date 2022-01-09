package com.gsastc.vtabd.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.archit.calendardaterangepicker.customviews.CalendarListener
import com.archit.calendardaterangepicker.customviews.DateRangeCalendarView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.gsastc.vtabd.R
import com.gsastc.vtabd.SharedPrefManager
import com.gsastc.vtabd.adapter.GuideAdapter
import com.gsastc.vtabd.model.DataModel
import kotlinx.android.synthetic.main.fragment_guide.*
import kotlinx.android.synthetic.main.fragment_guide.view.*
import kotlinx.android.synthetic.main.select_date_dialog.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class GuideFragment : Fragment() {
    private var recyclerView: RecyclerView? = null
    private var shimmerFrameLayout: ShimmerFrameLayout? = null
    private var placeAdapter: GuideAdapter? = null
    private var mHotel: MutableList<DataModel>? = null
    private var calendar: DateRangeCalendarView? = null
    private var dateRangeDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_guide, container, false)

        shimmerFrameLayout = view.findViewById(R.id.guide_shimmer_view_container) as ShimmerFrameLayout
        recyclerView = view.findViewById(R.id.guide_list)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        mHotel = ArrayList()
        retrieveGuides()

        return view
    }

    private fun retrieveGuides() {
        val guideRef = FirebaseDatabase.getInstance().reference.child("guide")
        guideRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mHotel?.clear()
                for (snapshot in dataSnapshot.children) {
                    val guides = snapshot.getValue(DataModel::class.java)
                    if (guides != null) {
                        mHotel?.add(guides)
                        placeAdapter = context?.let { GuideAdapter(it, mHotel as ArrayList<DataModel>) }
                        recyclerView?.adapter = placeAdapter
                        empty_guide?.visibility = View.GONE
                        recyclerView?.visibility = View.VISIBLE
                        shimmerFrameLayout?.stopShimmer()
                        shimmerFrameLayout?.visibility = View.GONE
                    }
                }
                placeAdapter?.notifyDataSetChanged()
                if (mHotel?.size == 0) {
                    empty_guide?.visibility = View.VISIBLE
                    recyclerView?.visibility = View.GONE
                    shimmerFrameLayout?.stopShimmer()
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

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            requireFragmentManager().beginTransaction().detach(this).attach(this).commit()
        }
    }
}
