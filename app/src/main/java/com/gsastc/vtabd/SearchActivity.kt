package com.gsastc.vtabd

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.gsastc.vtabd.adapter.*
import com.gsastc.vtabd.model.DataModel
import com.gsastc.vtabd.utils.toasty
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener
import kotlinx.android.synthetic.main.activity_search.*


class SearchActivity : AppCompatActivity() {
    private var type: String = "Select Type"
    private var district: String = "Select District"

    //blog
    private var blogRecyclerView: RecyclerView? = null
    private var blogAdapter: BlogAdapter? = null
    private var blogDataList: MutableList<DataModel>? = null

    //district
    private var districtRecyclerView: RecyclerView? = null
    private var districtAdapter: CityAdapter? = null
    private var districtDataList: MutableList<DataModel>? = null

    //place
    private var placeRecyclerView: RecyclerView? = null
    private var placeAdapter: PlaceAdapter? = null
    private var placeDataList: MutableList<DataModel>? = null

    //hotel
    private var hotelRecyclerView: RecyclerView? = null
    private var hotelAdapter: HotelAdapter? = null
    private var hotelDataList: MutableList<DataModel>? = null

    //transport
    private var transportRecyclerView: RecyclerView? = null
    private var transportAdapter: TransportAdapter? = null
    private var transportDataList: MutableList<DataModel>? = null

    //guide
    private var guideRecyclerView: RecyclerView? = null
    private var guideAdapter: GuideAdapter? = null
    private var guideDataList: MutableList<DataModel>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        close_search?.setOnClickListener {
            finish()
        }

        type_spinner?.setOnSpinnerItemSelectedListener(OnSpinnerItemSelectedListener<String?> { _, _, _, newItem ->
            type = newItem.toString()
        })

        district_spinner?.setOnSpinnerItemSelectedListener(OnSpinnerItemSelectedListener<String?> { _, _, _, newItem ->
            district = newItem.toString()
        })

        if (type != "Select Type" && district != "Select District") {
            search?.isClickable = true
        }

        search?.setOnClickListener {
            val loading = ProgressDialog.show(
                this,
                "",
                "Please wait...",
                false,
                false
            )
            when (type) {
                "Blog" -> {
                    blogRecyclerView = findViewById(R.id.blog_list)
                    blogRecyclerView?.setHasFixedSize(true)
                    blogRecyclerView?.layoutManager = LinearLayoutManager(this)
                    blogDataList = ArrayList()
                    retrieveBlog(loading)
                }
                "Districts" -> {
                    districtRecyclerView = findViewById(R.id.district_list)
                    districtRecyclerView?.setHasFixedSize(true)
                    districtRecyclerView?.layoutManager = LinearLayoutManager(this)
                    districtDataList = ArrayList()
                    retrieveDistrict(loading)

                }
                "Locations" -> {
                    placeRecyclerView = findViewById(R.id.location_list)
                    placeRecyclerView?.setHasFixedSize(true)
                    placeRecyclerView?.layoutManager = LinearLayoutManager(this)
                    placeDataList = ArrayList()
                    retrieveLocation(loading)
                }
                "Hotel" -> {
                    hotelRecyclerView = findViewById(R.id.hotel_list)
                    hotelRecyclerView?.setHasFixedSize(true)
                    hotelRecyclerView?.layoutManager = LinearLayoutManager(this)
                    hotelDataList = ArrayList()
                    retrieveHotel(loading)
                }
                "Transport" -> {
                    transportRecyclerView = findViewById(R.id.transport_list)
                    transportRecyclerView?.setHasFixedSize(true)
                    transportRecyclerView?.layoutManager = LinearLayoutManager(this)
                    transportDataList = ArrayList()
                    retrieveTransport(loading)
                }
                "Guide" -> {
                    guideRecyclerView = findViewById(R.id.guide_list)
                    guideRecyclerView?.setHasFixedSize(true)
                    guideRecyclerView?.layoutManager = LinearLayoutManager(this)
                    guideDataList = ArrayList()
                    retrieveGuide(loading)
                }
                else -> {
                    toasty(this, "Select type, which you want.")
                    loading.dismiss()
                }
            }
        }
    }

    private fun retrieveBlog(loading: ProgressDialog) {
        val blogRef = FirebaseDatabase.getInstance().reference.child("blog")
        val query: Query = blogRef.orderByChild("city").equalTo(district)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                blogDataList?.clear()
                for (snapshot in dataSnapshot.children) {
                    val model = snapshot.getValue(DataModel::class.java)
                    if (model != null) {
                        blogDataList?.add(model)
                        blogAdapter =
                            BlogAdapter(this@SearchActivity, blogDataList as ArrayList<DataModel>)
                        blogRecyclerView?.adapter = blogAdapter
                        blogRecyclerView?.visibility = View.VISIBLE
                        districtRecyclerView?.visibility = View.GONE
                        placeRecyclerView?.visibility = View.GONE
                        hotelRecyclerView?.visibility = View.GONE
                        transportRecyclerView?.visibility = View.GONE
                        guideRecyclerView?.visibility = View.GONE
                        empty_result?.visibility = View.GONE
                        loading.dismiss()
                    }
                }
                blogAdapter?.notifyDataSetChanged()

                if (blogDataList?.size == 0) {
                    empty_result?.visibility = View.VISIBLE
                    blogRecyclerView?.visibility = View.GONE
                    districtRecyclerView?.visibility = View.GONE
                    placeRecyclerView?.visibility = View.GONE
                    hotelRecyclerView?.visibility = View.GONE
                    transportRecyclerView?.visibility = View.GONE
                    guideRecyclerView?.visibility = View.GONE
                    loading.dismiss()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                loading.dismiss()
            }

        })
    }

    private fun retrieveDistrict(loading: ProgressDialog) {
        val districtRef = FirebaseDatabase.getInstance().reference.child("City")
        val query: Query = districtRef.orderByChild("cityName").equalTo(district)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                districtDataList?.clear()
                for (snapshot in dataSnapshot.children) {
                    val model = snapshot.getValue(DataModel::class.java)
                    if (model != null) {
                        districtDataList?.add(model)
                        districtAdapter = CityAdapter(
                            this@SearchActivity,
                            districtDataList as ArrayList<DataModel>
                        )
                        districtRecyclerView?.adapter = districtAdapter
                        blogRecyclerView?.visibility = View.GONE
                        districtRecyclerView?.visibility = View.VISIBLE
                        placeRecyclerView?.visibility = View.GONE
                        hotelRecyclerView?.visibility = View.GONE
                        transportRecyclerView?.visibility = View.GONE
                        guideRecyclerView?.visibility = View.GONE
                        empty_result?.visibility = View.GONE
                        loading.dismiss()
                    }
                }
                districtAdapter?.notifyDataSetChanged()

                if (districtDataList?.size == 0) {
                    empty_result?.visibility = View.VISIBLE
                    blogRecyclerView?.visibility = View.GONE
                    districtRecyclerView?.visibility = View.GONE
                    placeRecyclerView?.visibility = View.GONE
                    hotelRecyclerView?.visibility = View.GONE
                    transportRecyclerView?.visibility = View.GONE
                    guideRecyclerView?.visibility = View.GONE
                    loading.dismiss()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                loading.dismiss()
            }

        })
    }

    private fun retrieveLocation(loading: ProgressDialog) {
        val hotelRef = FirebaseDatabase.getInstance().reference.child("Place")
        val query: Query = hotelRef.orderByChild("city").equalTo(district)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                placeDataList?.clear()
                for (snapshot in dataSnapshot.children) {
                    val model = snapshot.getValue(DataModel::class.java)
                    if (model != null) {
                        placeDataList?.add(model)
                        placeAdapter =
                            PlaceAdapter(this@SearchActivity, placeDataList as ArrayList<DataModel>)
                        placeRecyclerView?.adapter = placeAdapter
                        blogRecyclerView?.visibility = View.GONE
                        districtRecyclerView?.visibility = View.GONE
                        placeRecyclerView?.visibility = View.VISIBLE
                        hotelRecyclerView?.visibility = View.GONE
                        transportRecyclerView?.visibility = View.GONE
                        guideRecyclerView?.visibility = View.GONE
                        empty_result?.visibility = View.GONE
                        loading.dismiss()
                    }
                }
                placeAdapter?.notifyDataSetChanged()

                if (placeDataList?.size == 0) {
                    empty_result?.visibility = View.VISIBLE
                    blogRecyclerView?.visibility = View.GONE
                    districtRecyclerView?.visibility = View.GONE
                    placeRecyclerView?.visibility = View.GONE
                    hotelRecyclerView?.visibility = View.GONE
                    transportRecyclerView?.visibility = View.GONE
                    guideRecyclerView?.visibility = View.GONE
                    loading.dismiss()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                loading.dismiss()
            }

        })
    }

    private fun retrieveHotel(loading: ProgressDialog) {
        val hotelRef = FirebaseDatabase.getInstance().reference.child("Hotel")
        val query: Query = hotelRef.orderByChild("cityName").equalTo(district)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                hotelDataList?.clear()
                for (snapshot in dataSnapshot.children) {
                    val model = snapshot.getValue(DataModel::class.java)
                    if (model != null) {
                        hotelDataList?.add(model)
                        hotelAdapter =
                            HotelAdapter(this@SearchActivity, hotelDataList as ArrayList<DataModel>)
                        hotelRecyclerView?.adapter = hotelAdapter
                        blogRecyclerView?.visibility = View.GONE
                        districtRecyclerView?.visibility = View.GONE
                        placeRecyclerView?.visibility = View.GONE
                        hotelRecyclerView?.visibility = View.VISIBLE
                        transportRecyclerView?.visibility = View.GONE
                        guideRecyclerView?.visibility = View.GONE
                        empty_result?.visibility = View.GONE
                        loading.dismiss()
                    }
                }
                hotelAdapter?.notifyDataSetChanged()

                if (hotelDataList?.size == 0) {
                    empty_result?.visibility = View.VISIBLE
                    blogRecyclerView?.visibility = View.GONE
                    districtRecyclerView?.visibility = View.GONE
                    placeRecyclerView?.visibility = View.GONE
                    hotelRecyclerView?.visibility = View.GONE
                    transportRecyclerView?.visibility = View.GONE
                    guideRecyclerView?.visibility = View.GONE
                    loading.dismiss()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                loading.dismiss()
            }

        })
    }

    private fun retrieveTransport(loading: ProgressDialog) {
        val transportRef = FirebaseDatabase.getInstance().reference.child("rider")
        val query: Query = transportRef.orderByChild("cityName").equalTo(district)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                transportDataList?.clear()
                for (snapshot in dataSnapshot.children) {
                    val model = snapshot.getValue(DataModel::class.java)
                    if (model != null) {
                        transportDataList?.add(model)
                        transportAdapter = TransportAdapter(
                            this@SearchActivity,
                            transportDataList as ArrayList<DataModel>
                        )
                        transportRecyclerView?.adapter = transportAdapter
                        blogRecyclerView?.visibility = View.GONE
                        districtRecyclerView?.visibility = View.GONE
                        placeRecyclerView?.visibility = View.GONE
                        hotelRecyclerView?.visibility = View.GONE
                        transportRecyclerView?.visibility = View.VISIBLE
                        guideRecyclerView?.visibility = View.GONE
                        empty_result?.visibility = View.GONE
                        loading.dismiss()
                    }
                }
                transportAdapter?.notifyDataSetChanged()

                if (transportDataList?.size == 0) {
                    empty_result?.visibility = View.VISIBLE
                    blogRecyclerView?.visibility = View.GONE
                    districtRecyclerView?.visibility = View.GONE
                    placeRecyclerView?.visibility = View.GONE
                    hotelRecyclerView?.visibility = View.GONE
                    transportRecyclerView?.visibility = View.GONE
                    guideRecyclerView?.visibility = View.GONE
                    loading.dismiss()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                loading.dismiss()
            }

        })
    }

    private fun retrieveGuide(loading: ProgressDialog) {
        val hotelRef = FirebaseDatabase.getInstance().reference.child("guide")
        val query: Query = hotelRef.orderByChild("cityName").equalTo(district)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                guideDataList?.clear()
                for (snapshot in dataSnapshot.children) {
                    val model = snapshot.getValue(DataModel::class.java)
                    if (model != null) {
                        guideDataList?.add(model)
                        guideAdapter =
                            GuideAdapter(this@SearchActivity, guideDataList as ArrayList<DataModel>)
                        guideRecyclerView?.adapter = guideAdapter
                        blogRecyclerView?.visibility = View.GONE
                        districtRecyclerView?.visibility = View.GONE
                        placeRecyclerView?.visibility = View.GONE
                        hotelRecyclerView?.visibility = View.GONE
                        transportRecyclerView?.visibility = View.GONE
                        guideRecyclerView?.visibility = View.VISIBLE
                        empty_result?.visibility = View.GONE
                        loading.dismiss()
                    }
                }
                guideAdapter?.notifyDataSetChanged()

                if (guideDataList?.size == 0) {
                    empty_result?.visibility = View.VISIBLE
                    blogRecyclerView?.visibility = View.GONE
                    districtRecyclerView?.visibility = View.GONE
                    placeRecyclerView?.visibility = View.GONE
                    hotelRecyclerView?.visibility = View.GONE
                    transportRecyclerView?.visibility = View.GONE
                    guideRecyclerView?.visibility = View.GONE
                    loading.dismiss()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                loading.dismiss()
            }

        })
    }
}