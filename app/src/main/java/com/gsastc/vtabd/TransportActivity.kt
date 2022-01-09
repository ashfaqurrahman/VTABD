package com.gsastc.vtabd

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.gsastc.vtabd.adapter.SystemVehicleAdapter
import com.gsastc.vtabd.model.DataModel
import com.gsastc.vtabd.utils.logoutUser
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_transport.*
import java.util.ArrayList

class TransportActivity : AppCompatActivity(), View.OnClickListener {
    private var mAuth: FirebaseAuth? = null
    private var transportRef: DatabaseReference? = null
    private var currentUserID: String? = null
    private var city: String? = null
    private var recyclerView: RecyclerView? = null
    private var shimmerFrameLayout: ShimmerFrameLayout? = null
    private var vehicleAdapter: SystemVehicleAdapter? = null
    private var mVehicle: MutableList<DataModel>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transport)

        mAuth = FirebaseAuth.getInstance()
        currentUserID = mAuth?.currentUser?.uid
        transportRef = FirebaseDatabase.getInstance().reference.child("transport")

        val collapsingToolbar = findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)

        if (SharedPrefManager.getInstance(this)!!.getUserName == null || SharedPrefManager.getInstance(
                this
            )!!.getUserPic == null){
            val myData = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val data = dataSnapshot.getValue(DataModel::class.java)
                    if (data?.transportName != null && data.imageUrl != null) {
                        city = data.cityName
                        SharedPrefManager.getInstance(this@TransportActivity)!!.saveHotelImageNameAddress(data.imageUrl.toString(), data.transportName.toString(), data.cityName + ", " + data.division.toString())
                        collapsingToolbar.title = SharedPrefManager.getInstance(this@TransportActivity)!!.getHotelName
                        transport_address.text = SharedPrefManager.getInstance(this@TransportActivity)!!.getHotelAddress
                        Picasso.get().load(SharedPrefManager.getInstance(this@TransportActivity)!!.getHotelImageUrl)
                            .placeholder(R.drawable.empty).into(transport_image)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(
                        this@TransportActivity,
                        "Please give your hotel Image",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            transportRef?.child(currentUserID.toString())?.addValueEventListener(myData)
        } else {
            collapsingToolbar.title = SharedPrefManager.getInstance(this@TransportActivity)!!.getHotelName
            transport_address.text = SharedPrefManager.getInstance(this@TransportActivity)!!.getHotelAddress
            Picasso.get().load(SharedPrefManager.getInstance(this@TransportActivity)!!.getHotelImageUrl)
                .placeholder(R.drawable.empty).into(transport_image)
        }

        hotel_fab.setButtonIconResource(R.drawable.ic_baseline_local_car_wash_24)
        hotel_fab.setOnClickListener {
            val intent = Intent(this@TransportActivity, AddVehicleActivity::class.java)
            intent.putExtra("city", city)
            startActivity(intent)
        }

        reservation?.setOnClickListener(this)
        log_out?.setOnClickListener(this)

        val bitmap = (transport_image.drawable as BitmapDrawable).bitmap
        window.statusBarColor = getDominantColor(bitmap)
        toolbar.setTitleTextColor(getDominantContrasColor(bitmap))
        val mAppBarLayout = findViewById<View>(R.id.app_bar_layout) as AppBarLayout
        mAppBarLayout.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            var isShow = false
            var scrollRange = -1

            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                // when create page first time...
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                    collapsingToolbar.title =
                        SharedPrefManager.getInstance(this@TransportActivity)!!.getHotelName
                    collapsingToolbar.setExpandedTitleColor(resources.getColor(R.color.white))
                }
                // when scrollbar on top...Collapsed
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.title =
                        SharedPrefManager.getInstance(this@TransportActivity)!!.getHotelName
                    toolbar.setBackgroundColor(getDominantColor(bitmap))
                    collapsingToolbar.setCollapsedTitleTextColor(Color.BLACK)
                    transport_address.visibility = View.GONE
                    isShow = true
                }
                // when scroll bar on down side...Expanded
                else if (isShow) {
                    collapsingToolbar.title =
                        SharedPrefManager.getInstance(this@TransportActivity)!!.getHotelName
                    toolbar.setBackgroundColor(Color.TRANSPARENT)
                    collapsingToolbar.setExpandedTitleColor(resources.getColor(R.color.white))
                    transport_address.visibility = View.VISIBLE
                    isShow = false
                }
            }
        })

        shimmerFrameLayout = findViewById(R.id.vehicle_shimmer_view_container)
        recyclerView = findViewById(R.id.vehicles_list)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        mVehicle = ArrayList()
        retrieveVehicle()
    }

    private fun retrieveVehicle() {
        val vehicleRef = FirebaseDatabase.getInstance().reference.child("vehicle")
        val queue = vehicleRef.orderByChild("transportID").equalTo(currentUserID)
        queue.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mVehicle?.clear()
                for (snapshot in dataSnapshot.children) {
                    val model = snapshot.getValue(DataModel::class.java)
                    if (model != null) {
                        mVehicle?.add(model)
                        vehicleAdapter = SystemVehicleAdapter(this@TransportActivity, mVehicle as ArrayList<DataModel>)
                        recyclerView?.adapter = vehicleAdapter
                        shimmerFrameLayout?.stopShimmer()
                        shimmerFrameLayout?.visibility = View.GONE
                        vehicle_layout?.visibility = View.VISIBLE
                        empty_vehicles?.visibility = View.GONE
                    }
                }
                vehicleAdapter?.notifyDataSetChanged()
                if (mVehicle?.size == 0) {
                    shimmerFrameLayout?.stopShimmer()
                    shimmerFrameLayout?.visibility = View.GONE
                    empty_vehicles?.visibility = View.VISIBLE
                    vehicle_layout?.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun onClick(p0: View?) {
        when(p0!!.id) {
            R.id.reservation -> {
                startActivity(Intent(this, VehicleReservationActivity::class.java))
            }
            R.id.log_out -> {
                logoutUser(this@TransportActivity)
            }
        }
    }

    private fun getDominantColor(bitmap: Bitmap): Int {
        val bitMap = Bitmap.createScaledBitmap(bitmap, 1, 1, true)
        val color = bitMap.getPixel(0, 0)
        bitMap.recycle()
        return color
    }

    private fun getDominantContrasColor(bitmap: Bitmap): Int {
        val bitMap = Bitmap.createScaledBitmap(bitmap, 1, 1, true)
        val color = bitMap.getPixel(0, 0)
        bitMap.recycle()
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        return Color.rgb(255 - red, 255 - green, 255 - blue)
    }
}