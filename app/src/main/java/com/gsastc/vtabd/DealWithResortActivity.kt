package com.gsastc.vtabd

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.database.*
import com.gsastc.vtabd.adapter.GuideAdapter
import com.gsastc.vtabd.adapter.HotelAdapter
import com.gsastc.vtabd.adapter.TransportAdapter
import com.gsastc.vtabd.model.DataModel
import com.gsastc.vtabd.utils.hideKeyboard
import kotlinx.android.synthetic.main.activity_deal_with_resort.*
import java.util.*

@Suppress("DEPRECATION")
class DealWithResortActivity : AppCompatActivity(), View.OnClickListener {

    private var recyclerView: RecyclerView? = null
    private var shimmerFrameLayout: ShimmerFrameLayout? = null
    private var hotelAdapter: HotelAdapter? = null
    private var hotelModel: MutableList<DataModel>? = null
    private var privateTransportAdapter: TransportAdapter? = null
    private var privateTransportModel: MutableList<DataModel>? = null
    private var guideAdapter: GuideAdapter? = null
    private var guideModel: MutableList<DataModel>? = null
    private lateinit var hireStatus: String
    private lateinit var dataRef: DatabaseReference

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deal_with_resort)

        hotel_btn!!.setOnClickListener(this)
        transport_btn!!.setOnClickListener(this)
        guide_btn!!.setOnClickListener(this)

        back_place_details?.setOnClickListener {
            finish()
        }

        when(intent.getStringExtra("hire")) {
             "hotel" -> {
                hireStatus = "hotel"
                hotel_btn.alpha = 1f
                transport_btn.alpha = 0.5f
                guide_btn.alpha = 0.5f
            }
            "vehicle" -> {
                hireStatus = "vehicle"
                hotel_btn.alpha = 0.5f
                transport_btn.alpha = 1f
                guide_btn.alpha = 0.5f
            }
            "guide" -> {
                hireStatus = "guide"
                hotel_btn.alpha = 0.5f
                transport_btn.alpha = 0.5f
                guide_btn.alpha = 1f
            }
        }

        val loading = ProgressDialog.show(
            this,
            "",
            "Please wait...",
            false,
            false
        )
        retrieveHotelInBottomSheet(loading, hireStatus, intent.getStringExtra("city"))

        shimmerFrameLayout = findViewById(R.id.bottom_sheet_hotel_shimmer_view_container)
        recyclerView = findViewById(R.id.bottom_sheet_hotel_list)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(this)
    }

    override fun onClick(p0: View?) {
        val loading = ProgressDialog.show(
            this,
            "",
            "Please wait...",
            false,
            false
        )
        when(p0!!.id) {
            R.id.hotel_btn -> {
                hideKeyboard(this)
                hireStatus = "hotel"
                hotel_btn.alpha = 1f
                transport_btn.alpha = 0.5f
                guide_btn.alpha = 0.5f
                retrieveHotelInBottomSheet(loading, hireStatus, intent.getStringExtra("city"))
            }
            R.id.transport_btn -> {
                hireStatus = "vehicle"
                hotel_btn.alpha = 0.5f
                transport_btn.alpha = 1f
                guide_btn.alpha = 0.5f
                retrieveHotelInBottomSheet(loading, hireStatus, intent.getStringExtra("city"))
            }
            R.id.guide_btn -> {
                hireStatus = "guide"
                hotel_btn.alpha = 0.5f
                transport_btn.alpha = 0.5f
                guide_btn.alpha = 1f
                retrieveHotelInBottomSheet(loading, hireStatus, intent.getStringExtra("city"))
            }
        }
    }

    private fun retrieveHotelInBottomSheet(loading: ProgressDialog, hireStatus: String, city: String?) {
        if (hireStatus == "hotel") {
            hotelModel = ArrayList()
            dataRef = FirebaseDatabase.getInstance().reference.child("Hotel")
            val query: Query = dataRef.orderByChild("cityName").equalTo(city)
            query.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    hotelModel?.clear()
                    for (snapshot in dataSnapshot.children) {
                        val hotels = snapshot.getValue(DataModel::class.java)
                        if (hotels != null) {
                            hotelModel?.add(hotels)
                            hotelAdapter = HotelAdapter(this@DealWithResortActivity, hotelModel as ArrayList<DataModel>)
                            recyclerView?.adapter = hotelAdapter
                            shimmerFrameLayout?.stopShimmer()
                            shimmerFrameLayout?.visibility = View.GONE
                            main_layout?.visibility = View.VISIBLE
                            empty_list?.visibility = View.GONE
                            search_for?.text = "Hotel list for " + intent.getStringExtra("city")
                            loading.dismiss()
                        }
                    }
                    hotelAdapter?.notifyDataSetChanged()
                    if (hotelModel?.size == 0) {
                        loading.dismiss()
                        empty_list?.visibility = View.VISIBLE
                        main_layout?.visibility = View.GONE
                        shimmerFrameLayout?.stopShimmer()
                        shimmerFrameLayout?.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    loading.dismiss()
                }

            })
        }
        if (hireStatus == "vehicle") {
            privateTransportModel = ArrayList()
            dataRef = FirebaseDatabase.getInstance().reference.child("vehicle")
            val query: Query = dataRef.orderByChild("cityName").equalTo(city)
            query.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    privateTransportModel?.clear()
                    for (snapshot in dataSnapshot.children) {
                        val model = snapshot.getValue(DataModel::class.java)
                        if (model != null) {
                            privateTransportModel?.add(model)
                            privateTransportAdapter = TransportAdapter(this@DealWithResortActivity, privateTransportModel as ArrayList<DataModel>)
                            recyclerView?.adapter = privateTransportAdapter
                            shimmerFrameLayout?.stopShimmer()
                            shimmerFrameLayout?.visibility = View.GONE
                            main_layout?.visibility = View.VISIBLE
                            empty_list?.visibility = View.GONE
                            search_for?.text = "Transport list for " + intent.getStringExtra("city")
                            loading.dismiss()
                        }
                    }
                    privateTransportAdapter?.notifyDataSetChanged()
                    if (privateTransportModel?.size == 0) {
                        loading.dismiss()
                        empty_list?.visibility = View.VISIBLE
                        main_layout?.visibility = View.GONE
                        shimmerFrameLayout?.stopShimmer()
                        shimmerFrameLayout?.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    loading.dismiss()
                }

            })
        }
        if (hireStatus == "guide") {
            guideModel = ArrayList()
            dataRef = FirebaseDatabase.getInstance().reference.child("guide")
            val query: Query = dataRef.orderByChild("cityName").equalTo(city)
            query.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    guideModel?.clear()
                    for (snapshot in dataSnapshot.children) {
                        val model = snapshot.getValue(DataModel::class.java)
                        if (model != null) {
                            guideModel?.add(model)
                            guideAdapter = GuideAdapter(this@DealWithResortActivity, guideModel as ArrayList<DataModel>)
                            recyclerView?.adapter = guideAdapter
                            shimmerFrameLayout?.stopShimmer()
                            shimmerFrameLayout?.visibility = View.GONE
                            main_layout?.visibility = View.VISIBLE
                            empty_list?.visibility = View.GONE
                            search_for?.text = "Guide list for " + intent.getStringExtra("city")
                            loading.dismiss()
                        }
                    }
                    guideAdapter?.notifyDataSetChanged()
                    if (guideModel?.size == 0) {
                        loading.dismiss()
                        empty_list?.visibility = View.VISIBLE
                        main_layout?.visibility = View.GONE
                        shimmerFrameLayout?.stopShimmer()
                        shimmerFrameLayout?.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    loading.dismiss()
                }

            })
        }

    }
}