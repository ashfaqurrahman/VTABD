package com.gsastc.vtabd

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.gsastc.vtabd.adapter.SystemRoomAdapter
import com.gsastc.vtabd.model.DataModel
import com.gsastc.vtabd.utils.logoutUser
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_hotel_owner.*
import kotlinx.android.synthetic.main.activity_hotel_owner.empty_rooms
import kotlinx.android.synthetic.main.activity_hotel_owner.room_layout
import kotlinx.android.synthetic.main.activity_hotel_owner.toolbar
import java.util.*


class HotelOwnerActivity : AppCompatActivity(), View.OnClickListener {
    private var mAuth: FirebaseAuth? = null
    private var hotelRef: DatabaseReference? = null
    private var currentUserID: String? = null
    private var activeToast: Toast? = null
    private var recyclerView: RecyclerView? = null
    private var shimmerFrameLayout: ShimmerFrameLayout? = null
    private var rooAdapter: SystemRoomAdapter? = null
    private var mRooms: MutableList<DataModel>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hotel_owner)

        mAuth = FirebaseAuth.getInstance()
        currentUserID = mAuth?.currentUser?.uid
        hotelRef = FirebaseDatabase.getInstance().reference.child("Hotel")

        val collapsingToolbar = findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)

        if (SharedPrefManager.getInstance(this)!!.getUserName == null || SharedPrefManager.getInstance(
                this
            )!!.getUserPic == null){
            val myData = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val data = dataSnapshot.getValue(DataModel::class.java)
                    if (data?.hotelName != null && data.imageUrl != null) {
                        SharedPrefManager.getInstance(this@HotelOwnerActivity)!!.saveHotelImageNameAddress(data.imageUrl.toString(), data.hotelName.toString(), data.cityName + ", " + data.division.toString())
                        collapsingToolbar.title = SharedPrefManager.getInstance(this@HotelOwnerActivity)!!.getHotelName
                        hotel_address.text = SharedPrefManager.getInstance(this@HotelOwnerActivity)!!.getHotelAddress
                        Picasso.get().load(SharedPrefManager.getInstance(this@HotelOwnerActivity)!!.getHotelImageUrl)
                            .placeholder(R.drawable.empty).into(hotel_image)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(
                        this@HotelOwnerActivity,
                        "Please give your hotel Image",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            hotelRef?.child(currentUserID.toString())?.addValueEventListener(myData)
        } else {
            collapsingToolbar.title = SharedPrefManager.getInstance(this@HotelOwnerActivity)!!.getHotelName
            hotel_address.text = SharedPrefManager.getInstance(this@HotelOwnerActivity)!!.getHotelAddress
            Picasso.get().load(SharedPrefManager.getInstance(this@HotelOwnerActivity)!!.getHotelImageUrl)
                .placeholder(R.drawable.empty).into(hotel_image)
        }

        hotel_fab.setButtonIconResource(R.drawable.ic_city)
        hotel_fab.setOnClickListener {
            startActivity(Intent(this@HotelOwnerActivity, AddRoomActivity::class.java))
        }

        reservation?.setOnClickListener(this)
        log_out?.setOnClickListener(this)

        val bitmap = (hotel_image.drawable as BitmapDrawable).bitmap
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
                        SharedPrefManager.getInstance(this@HotelOwnerActivity)!!.getHotelName
                    collapsingToolbar.setExpandedTitleColor(resources.getColor(R.color.white))
                }
                // when scrollbar on top...Collapsed
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.title =
                        SharedPrefManager.getInstance(this@HotelOwnerActivity)!!.getHotelName
                    toolbar.setBackgroundColor(getDominantColor(bitmap))
                    collapsingToolbar.setCollapsedTitleTextColor(Color.BLACK)
                    hotel_address.visibility = View.GONE
                    isShow = true
                }
                // when scroll bar on down side...Expanded
                else if (isShow) {
                    collapsingToolbar.title =
                        SharedPrefManager.getInstance(this@HotelOwnerActivity)!!.getHotelName
                    toolbar.setBackgroundColor(Color.TRANSPARENT)
                    collapsingToolbar.setExpandedTitleColor(resources.getColor(R.color.white))
                    hotel_address.visibility = View.VISIBLE
                    isShow = false
                }
            }
        })

        shimmerFrameLayout = findViewById(R.id.room_shimmer_view_container)
        recyclerView = findViewById(R.id.room_list)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        mRooms = ArrayList()
        retrieveRooms()
    }

    private fun retrieveRooms() {
        val roomRef = FirebaseDatabase.getInstance().reference.child("Rooms")
        val queue = roomRef.orderByChild("hotelID").equalTo(currentUserID)
        queue.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mRooms?.clear()
                for (snapshot in dataSnapshot.children) {
                    val rooms = snapshot.getValue(DataModel::class.java)
                    if (rooms != null) {
                        mRooms?.add(rooms)
                        rooAdapter = SystemRoomAdapter(this@HotelOwnerActivity, mRooms as ArrayList<DataModel>)
                        recyclerView?.adapter = rooAdapter
                        shimmerFrameLayout?.stopShimmer()
                        shimmerFrameLayout?.visibility = View.GONE
                        room_layout?.visibility = View.VISIBLE
                        empty_rooms?.visibility = View.GONE
                    }
                }
                rooAdapter?.notifyDataSetChanged()
                if (mRooms?.size == 0) {
                    shimmerFrameLayout?.stopShimmer()
                    empty_rooms?.visibility = View.VISIBLE
                    room_layout?.visibility = View.GONE
                    shimmerFrameLayout?.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun onClick(p0: View?) {
        when(p0!!.id) {
            R.id.reservation -> {
                startActivity(Intent(this, RoomReservationsActivity::class.java))
            }
            R.id.log_out -> {
                logoutUser(this@HotelOwnerActivity)
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

    private fun toast(str: String) {
        activeToast?.cancel()
        activeToast = Toast.makeText(this, str, Toast.LENGTH_SHORT)
        activeToast?.show()
    }
}
